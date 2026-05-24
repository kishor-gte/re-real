/**
 * EstateRTC — real-time chat (STOMP) + voice (WebRTC) for agent/user and PG owner/user.
 */
(function (global) {
  'use strict';

  const EstateRTC = {
    ctx: '',
    mode: 'user',
    api: null,
    stomp: null,
    stompReady: false,
    _connectWaiters: [],
    me: null,
    session: null,
    replyToId: null,
    typingTimer: null,
    peerConnection: null,
    localStream: null,
    callSession: null,
    callTimerId: null,
    muted: false,
    onHold: false,
    soundsEnabled: true,
    _ringInterval: null,
    _ringCtx: null,
    _audioUnlocked: false,
    unread: { total: 0, enquiries: {}, bookings: {}, pgBookings: {} },
    _baseDocumentTitle: null,

    init(options) {
      this.ctx = (options.ctx || '').replace(/\/$/, '');
      this.mode = options.mode === 'agent' ? 'agent' : (options.mode === 'pg-owner' ? 'pg-owner' : 'user');
      this.api = options.api || (this.mode === 'agent' ? global.AgentAPI
        : this.mode === 'pg-owner' ? global.PgOwnerAPI : global.AuthAPI);
      if (this.mode === 'agent' && global.AgentAPI) {
        global.AgentAPI.base = this.ctx;
      }
      if (this.mode === 'pg-owner' && global.PgOwnerAPI) {
        global.PgOwnerAPI.base = this.ctx;
      }
      this.mountRtcRoot();
      this.bindUi();
      this.connectStomp();
      this.unlockAudioOnGesture();
      if ('Notification' in global && Notification.permission === 'default') {
        Notification.requestPermission().catch(function () {});
      }
      this._baseDocumentTitle = document.title;
      this.refreshUnreadSummary();
    },

    unlockAudioOnGesture() {
      const self = this;
      function unlock() {
        self._audioUnlocked = true;
        self.ensureAudioContext();
        document.removeEventListener('click', unlock);
        document.removeEventListener('keydown', unlock);
      }
      document.addEventListener('click', unlock, { once: false });
      document.addEventListener('keydown', unlock, { once: false });
    },

    bindUi() {
      const self = this;
      const sendBtn = document.getElementById('rtcSendBtn');
      const input = document.getElementById('rtcMessageInput');
      const closeBtn = document.getElementById('rtcChatClose');
      const minBtn = document.getElementById('rtcChatMin');
      const fileInput = document.getElementById('rtcFileInput');
      const replyCancel = document.getElementById('rtcReplyCancel');
      const acceptBtn = document.getElementById('rtcAcceptBtn');
      const rejectBtn = document.getElementById('rtcRejectBtn');
      const endBtn = document.getElementById('rtcEndCallBtn');
      const muteBtn = document.getElementById('rtcMuteBtn');
      const holdBtn = document.getElementById('rtcHoldBtn');
      const speakerBtn = document.getElementById('rtcSpeakerBtn');

      if (sendBtn) sendBtn.addEventListener('click', function () { self.sendMessage(); });
      if (input) {
        input.addEventListener('keydown', function (e) {
          if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); self.sendMessage(); }
        });
        input.addEventListener('input', function () { self.emitTyping(true); });
      }
      if (closeBtn) closeBtn.addEventListener('click', function () { self.hideChat(); });
      if (minBtn) minBtn.addEventListener('click', function () {
        document.getElementById('rtcChatPanel').classList.toggle('rtc-minimized');
      });
      if (fileInput) fileInput.addEventListener('change', function () { self.uploadFile(fileInput.files[0]); });
      if (replyCancel) replyCancel.addEventListener('click', function () { self.clearReply(); });
      if (acceptBtn) acceptBtn.addEventListener('click', function () { self.acceptCall(); });
      if (rejectBtn) rejectBtn.addEventListener('click', function () { self.rejectCall(); });
      if (endBtn) endBtn.addEventListener('click', function () { self.endCall('COMPLETED'); });
      if (muteBtn) muteBtn.addEventListener('click', function () { self.toggleMute(); });
      if (holdBtn) holdBtn.addEventListener('click', function () { self.toggleHold(); });
      if (speakerBtn) speakerBtn.addEventListener('click', function () {
        const audio = document.getElementById('rtcRemoteAudio');
        if (audio) audio.muted = !audio.muted;
      });
    },

    apiPath(url) {
      return url.startsWith('/') ? url : '/' + url;
    },

    request(url, options) {
      const path = this.apiPath(url);
      if (this.api && this.api.request) {
        if ((this.api === global.AgentAPI && global.AgentAPI.base)
          || (this.api === global.PgOwnerAPI && global.PgOwnerAPI.base)) {
          return this.api.request(path, options || {});
        }
        return this.api.request(this.ctx + path, options || {});
      }
      return fetch(this.ctx + path, Object.assign({ credentials: 'same-origin' }, options || {}))
        .then(function (r) { return r.json(); });
    },

    /** Keep chat/call UI on document.body so agent layout cannot clip or cover it. */
    mountRtcRoot() {
      const root = document.getElementById('rtcRoot');
      if (root && root.parentElement !== document.body) {
        document.body.appendChild(root);
      }
    },

    stompConnectHeaders() {
      const headers = {};
      const token = global.TokenStore && global.TokenStore.get();
      if (token) headers.Authorization = 'Bearer ' + token;
      return headers;
    },

    stompSocketUrl() {
      let url = this.ctx + '/ws/rtc';
      const token = global.TokenStore && global.TokenStore.get();
      if (token) url += (url.indexOf('?') >= 0 ? '&' : '?') + 'token=' + encodeURIComponent(token);
      return url;
    },

    stompClientClass() {
      if (global.StompJs && global.StompJs.Client) return global.StompJs.Client;
      if (global.Stomp && global.Stomp.Client) return global.Stomp.Client;
      return null;
    },

    connectStomp() {
      const self = this;
      const ClientClass = this.stompClientClass();
      if (!global.SockJS || !ClientClass) {
        console.warn('EstateRTC: SockJS/STOMP not loaded — chat will use HTTP fallback');
        return;
      }
      const client = new ClientClass({
        webSocketFactory: function () {
          return new SockJS(self.stompSocketUrl());
        },
        connectHeaders: self.stompConnectHeaders(),
        reconnectDelay: 4000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: function () { /* quiet */ },
        onConnect: function () {
          self.stompReady = true;
          self.onStompConnected(client);
          self._connectWaiters.forEach(function (fn) { fn(); });
          self._connectWaiters = [];
        },
        onDisconnect: function () {
          self.stompReady = false;
        },
        onStompError: function (frame) {
          console.warn('STOMP error', frame.headers && frame.headers.message);
          self.toast('Real-time connection error. Refresh the page.');
        },
        onWebSocketError: function () {
          self.toast('Could not connect to chat server.');
        }
      });
      client.activate();
      this.stomp = client;
    },

    whenStompReady() {
      const self = this;
      if (!this.stompClientClass()) {
        return Promise.resolve();
      }
      if (this.stompReady && this.stomp && this.stomp.connected) {
        return Promise.resolve();
      }
      return new Promise(function (resolve) {
        self._connectWaiters.push(resolve);
        setTimeout(resolve, 6000);
      });
    },

    async fetchMe() {
      try {
        const res = await this.request('/api/rtc/me');
        this.me = res.data || null;
        if (this.me && this.me.type) {
          this.me.type = String(this.me.type);
        }
      } catch (e) {
        console.warn('EstateRTC: could not load identity', e);
      }
    },

    subscribeCallChannels(client) {
      const self = this;
      if (this._callQueueSub) {
        try { this._callQueueSub.unsubscribe(); } catch (e) { /* ignore */ }
      }
      this._callQueueSub = client.subscribe('/user/queue/call', function (msg) {
        self.onCallSignal(JSON.parse(msg.body));
      });
      if (this.me && this.me.destinationUser) {
        const topic = '/topic/rtc.call.' + this.me.destinationUser;
        if (this._callTopicSub) {
          try { this._callTopicSub.unsubscribe(); } catch (e) { /* ignore */ }
        }
        this._callTopicSub = client.subscribe(topic, function (msg) {
          self.onCallSignal(JSON.parse(msg.body));
        });
      }
    },

    onStompConnected(client) {
      const self = this;
      client.subscribe('/topic/presence', function (msg) {
        self.onPresence(JSON.parse(msg.body));
      });
      client.subscribe('/user/queue/notifications', function (msg) {
        self.onNotification(JSON.parse(msg.body));
      });
      this.fetchMe().then(function () {
        self.subscribeCallChannels(client);
        self.refreshUnreadSummary();
      });
      if (self.session && self.session.roomId) {
        self.subscribeRoom(self.session.roomId);
      }
    },

    normalizeMessage(m) {
      if (!m) return m;
      const me = this.me || (this.session && this.session.me);
      if (me) {
        const myType = (me.type && me.type.name) ? me.type.name : String(me.type || '');
        const msgType = (m.senderType && m.senderType.name) ? m.senderType.name : String(m.senderType || '');
        m.mine = myType === msgType && String(me.id) === String(m.senderId);
      }
      return m;
    },

    isMe(type, id) {
      const me = this.me || (this.session && this.session.me);
      if (!me) return false;
      const myType = (me.type && me.type.name) ? me.type.name : String(me.type || '');
      const otherType = (type && type.name) ? type.name : String(type || '');
      return myType === otherType && String(me.id) === String(id);
    },

    cardActionsHtml(opts) {
      let peerType = 'AGENT';
      if (this.mode === 'agent' || this.mode === 'pg-owner') {
        peerType = 'USER';
      } else if (opts.pgBookingId != null && opts.pgBookingId !== '') {
        peerType = 'PG_OWNER';
      }
      const peerId = opts.peerId != null ? String(opts.peerId) : '';
      const peerName = esc(opts.peerName || 'Contact');
      let data = ' data-peer-name="' + escAttr(peerName) + '" data-property="' + escAttr(opts.propertyTitle || '') + '"' +
        ' data-peer-type="' + peerType + '" data-peer-id="' + peerId + '"';
      if (opts.enquiryId != null && opts.enquiryId !== '') {
        data += ' data-enquiry-id="' + escAttr(String(opts.enquiryId)) + '"';
      }
      if (opts.bookingId != null && opts.bookingId !== '') {
        data += ' data-booking-id="' + escAttr(String(opts.bookingId)) + '"';
      }
      if (opts.pgBookingId != null && opts.pgBookingId !== '') {
        data += ' data-pg-booking-id="' + escAttr(String(opts.pgBookingId)) + '"';
      }
      let badgeKey = '';
      if (opts.enquiryId != null && opts.enquiryId !== '') {
        badgeKey = 'enquiry-' + opts.enquiryId;
      } else if (opts.pgBookingId != null && opts.pgBookingId !== '') {
        badgeKey = 'pg-booking-' + opts.pgBookingId;
      } else if (opts.bookingId != null && opts.bookingId !== '') {
        badgeKey = 'booking-' + opts.bookingId;
      }
      const badgeHtml = badgeKey
        ? '<span class="rtc-unread-badge rtc-hidden" data-rtc-badge-key="' + escAttr(badgeKey) + '" aria-hidden="true"></span>'
        : '';
      return '<div class="rtc-card-actions">' +
        '<span class="rtc-presence" data-rtc-presence="' + peerType + '-' + peerId + '">' +
        '<span class="rtc-status-dot rtc-status-dot--offline"></span> <span class="rtc-presence-label">Offline</span></span>' +
        '<button type="button" class="rtc-btn-chat" data-rtc-chat="1"' + data + '>Chat Now' + badgeHtml + '</button>' +
        '<button type="button" class="rtc-btn-call" data-rtc-call="1"' + data + '>Voice Call</button>' +
        '</div>';
    },

    bindCardActions(root) {
      const self = this;
      if (!root || root.dataset.rtcActionsBound === '1') return;
      root.dataset.rtcActionsBound = '1';
      root.addEventListener('click', function (e) {
        const callBtn = e.target.closest('[data-rtc-call]');
        if (callBtn) {
          e.preventDefault();
          e.stopPropagation();
          self.callFromButton(callBtn);
          return;
        }
        const chatBtn = e.target.closest('[data-rtc-chat]');
        if (chatBtn) {
          e.preventDefault();
          e.stopPropagation();
          self.openFromButton(chatBtn);
        }
      });
      root.querySelectorAll('[data-rtc-presence]').forEach(function (el) {
        const key = el.getAttribute('data-rtc-presence');
        if (key && key.indexOf('-') > 0) {
          const dash = key.indexOf('-');
          const type = key.substring(0, dash);
          const id = key.substring(dash + 1);
          self.refreshPresence(type, id, el);
        }
      });
      self.renderAllUnreadBadges();
    },

    contextFromButton(btn) {
      const enquiryId = btn.getAttribute('data-enquiry-id');
      const bookingId = btn.getAttribute('data-booking-id');
      const pgBookingId = btn.getAttribute('data-pg-booking-id');
      return {
        enquiryId: enquiryId && enquiryId !== '' ? enquiryId : null,
        bookingId: bookingId && bookingId !== '' ? bookingId : null,
        pgBookingId: pgBookingId && pgBookingId !== '' ? pgBookingId : null,
        peerName: btn.getAttribute('data-peer-name'),
        propertyTitle: btn.getAttribute('data-property')
      };
    },

    buildOpenBody(opts) {
      const body = {};
      if (opts.enquiryId != null && opts.enquiryId !== '') {
        body.enquiryId = Number(opts.enquiryId);
      }
      if (opts.bookingId != null && opts.bookingId !== '') {
        body.bookingId = Number(opts.bookingId);
      }
      if (opts.pgBookingId != null && opts.pgBookingId !== '') {
        body.pgBookingId = Number(opts.pgBookingId);
      }
      if (!body.enquiryId && !body.bookingId && !body.pgBookingId) {
        throw new Error('Missing enquiry, booking, or PG booking reference');
      }
      return body;
    },

    applySessionData(session) {
      this.session = session;
      if (this.session.me && this.session.me.type && typeof this.session.me.type === 'object') {
        this.session.me.type = this.session.me.type.name || this.session.me.type;
      }
      if (this.session.peerType && typeof this.session.peerType === 'object') {
        this.session.peerType = this.session.peerType.name || this.session.peerType;
      }
      if (this.session.me) {
        this.me = this.session.me;
      }
      return this.session;
    },

    async ensureRoomSession(opts) {
      const body = this.buildOpenBody(opts);
      const sameRoom = this.session && this.session.roomId && (
        (body.enquiryId && Number(this.session.enquiryId) === body.enquiryId) ||
        (body.bookingId && Number(this.session.bookingId) === body.bookingId) ||
        (body.pgBookingId && Number(this.session.pgBookingId) === body.pgBookingId)
      );
      if (sameRoom) {
        return this.session;
      }
      const res = await this.request('/api/rtc/chat/open', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      return this.applySessionData(res.data);
    },

    openFromButton(btn) {
      this.openChat(this.contextFromButton(btn));
    },

    callFromButton(btn) {
      this.startVoiceCall(this.contextFromButton(btn));
    },

    async openChat(opts) {
      const self = this;
      const peerName = opts.peerName || 'Chat';
      const propertyTitle = opts.propertyTitle || '';
      try {
        self.showChat(peerName, propertyTitle);
        const box = document.getElementById('rtcMessages');
        if (box) box.innerHTML = '<p class="rtc-connecting">Opening chat…</p>';
        await self.ensureRoomSession(opts);
        await self.fetchMe();
        if (self.session.peerName) {
          document.getElementById('rtcPeerName').textContent = opts.peerName || self.session.peerName;
        }
        if (self.session.propertyTitle) {
          document.getElementById('rtcPeerSub').textContent = opts.propertyTitle || self.session.propertyTitle;
        }
        const msgs = (self.session.messages || []).map(function (m) { return self.normalizeMessage(m); });
        self.renderMessages(msgs);
        await self.whenStompReady();
        self.subscribeRoom(self.session.roomId);
        self.markSeen();
        self.clearUnreadForSession();
        self.updatePeerPresence(self.session.peerPresence);
        const input = document.getElementById('rtcMessageInput');
        if (input) input.focus();
      } catch (err) {
        self.hideChat();
        self.toast(err.message || 'Could not open chat', 'error');
      }
    },

    subscribeRoom(roomId) {
      const self = this;
      if (!roomId) return;
      if (!this.stomp || !this.stomp.connected) {
        this.whenStompReady().then(function () { self.subscribeRoom(roomId); });
        return;
      }
      const topic = '/topic/chat.room.' + roomId;
      if (this._roomSub) {
        try { this._roomSub.unsubscribe(); } catch (e) { /* ignore */ }
      }
      this._roomSub = this.stomp.subscribe(topic, function (msg) {
        let payload;
        try { payload = JSON.parse(msg.body); } catch (e) { return; }
        if (payload.type === 'SEEN' || payload.type === 'DELETED' || payload.type === 'STATUS') {
          self.handleMeta(payload);
          return;
        }
        if (payload.typing !== undefined && payload.participantType) {
          if (!self.isMe(payload.participantType, payload.participantId)) {
            const typingBar = document.getElementById('rtcTypingBar');
            if (payload.typing) typingBar.classList.remove('rtc-hidden');
            else typingBar.classList.add('rtc-hidden');
          }
          return;
        }
        if (payload.id) {
          payload = self.normalizeMessage(payload);
          const panel = document.getElementById('rtcChatPanel');
          const chatOpen = panel && !panel.classList.contains('rtc-hidden');
          const sameRoom = self.session && String(roomId) === String(self.session.roomId);
          if (sameRoom && chatOpen) {
            self.appendMessage(payload);
            if (!payload.mine) {
              self.playMessageBeep();
              self.markSeen();
              self.stomp.publish({
                destination: '/app/chat.delivered',
                body: JSON.stringify({ messageId: payload.id })
              });
            }
          } else if (!payload.mine) {
            self.playMessageBeep();
            self.notify('New message', payload.message || 'Attachment');
            self.bumpUnreadForRoom(roomId, 1);
            self.refreshUnreadSummary();
          }
        }
      });
    },

    showChat(peerName, propertyTitle) {
      this.mountRtcRoot();
      const panel = document.getElementById('rtcChatPanel');
      if (!panel) return;
      panel.classList.remove('rtc-hidden', 'rtc-minimized');
      document.getElementById('rtcPeerName').textContent = peerName || 'Chat';
      document.getElementById('rtcPeerSub').textContent = propertyTitle || '';
      const input = document.getElementById('rtcMessageInput');
      const sendBtn = document.getElementById('rtcSendBtn');
      if (input) { input.disabled = false; input.removeAttribute('readonly'); }
      if (sendBtn) sendBtn.disabled = false;
    },

    hideChat() {
      document.getElementById('rtcChatPanel').classList.add('rtc-hidden');
      if (this._roomSub) {
        try { this._roomSub.unsubscribe(); } catch (e) { /* ignore */ }
        this._roomSub = null;
      }
    },

    renderMessages(list) {
      const box = document.getElementById('rtcMessages');
      box.innerHTML = '';
      const self = this;
      (list || []).forEach(function (m) { self.appendMessage(m, true); });
      this.scrollMessages();
    },

    clearPendingBubbles() {
      const box = document.getElementById('rtcMessages');
      if (!box) return;
      box.querySelectorAll('[data-message-id^="pending-"]').forEach(function (el) { el.remove(); });
    },

    appendMessage(m, skipScroll) {
      if (!m || m.id == null) return;
      const box = document.getElementById('rtcMessages');
      if (!box) return;
      const idStr = String(m.id);
      if (box.querySelector('[data-message-id="' + idStr + '"]')) return;

      m = this.normalizeMessage(m);
      if (m.mine && idStr.indexOf('pending-') !== 0) {
        this.clearPendingBubbles();
      }
      const div = document.createElement('div');
      const pending = idStr.indexOf('pending-') === 0;
      div.className = 'rtc-bubble ' + (m.mine ? 'rtc-bubble--mine' : 'rtc-bubble--theirs') + (pending ? ' rtc-bubble--pending' : '');
      div.dataset.messageId = m.id;
      let html = '';
      if (m.replyToMessageId) html += '<div class="rtc-bubble-reply">Reply</div>';
      if (m.attachmentUrl) {
        if ((m.attachmentType || '').indexOf('image') >= 0) {
          html += '<img src="' + escAttr(this.ctx + m.attachmentUrl) + '" alt="attachment">';
        } else {
          html += '<a href="' + escAttr(this.ctx + m.attachmentUrl) + '" target="_blank" rel="noopener">Download attachment</a>';
        }
      }
      html += esc(m.message || '');
      html += '<div class="rtc-bubble-meta"><span>' + formatTime(m.sentAt) + '</span>';
      if (m.mine) html += '<span>' + (m.messageStatus || 'SENT') + '</span>';
      html += '<button type="button" class="rtc-icon-btn" data-reply="' + m.id + '" title="Reply">↩</button></div>';
      div.innerHTML = html;
      const replyBtn = div.querySelector('[data-reply]');
      if (replyBtn) {
        replyBtn.addEventListener('click', function () {
          EstateRTC.setReply(m.id, m.message);
        });
      }
      box.appendChild(div);
      if (!skipScroll) this.scrollMessages();
    },

    scrollMessages() {
      const box = document.getElementById('rtcMessages');
      if (box) box.scrollTop = box.scrollHeight;
    },

    async sendMessage() {
      const self = this;
      const input = document.getElementById('rtcMessageInput');
      const text = (input.value || '').trim();
      if (!text || !this.session) return;
      const payload = { roomId: this.session.roomId, message: text, replyToMessageId: this.replyToId };
      const pendingId = 'pending-' + Date.now();
      this.appendMessage({
        id: pendingId,
        message: text,
        mine: true,
        messageStatus: 'SENDING',
        sentAt: new Date().toISOString()
      });
      input.value = '';
      this.clearReply();
      this.emitTyping(false);

      try {
        await this.whenStompReady();
        if (this.stomp && this.stomp.connected) {
          this.stomp.publish({
            destination: '/app/chat.send',
            body: JSON.stringify(payload),
            headers: this.stompConnectHeaders()
          });
          return;
        }
        const res = await this.request('/api/rtc/chat/send', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        self.clearPendingBubbles();
        self.appendMessage(self.normalizeMessage(res.data));
      } catch (err) {
        self.clearPendingBubbles();
        self.toast(err.message || 'Could not send message', 'error');
      }
    },

    emitTyping(typing) {
      const self = this;
      if (!this.session || !this.stomp || !this.stomp.connected) return;
      clearTimeout(this.typingTimer);
      this.stomp.publish({
        destination: '/app/chat.typing',
        body: JSON.stringify({ roomId: this.session.roomId, typing: typing })
      });
      if (typing) {
        this.typingTimer = setTimeout(function () { self.emitTyping(false); }, 2000);
      }
    },

    markSeen() {
      if (!this.session || !this.stomp || !this.stomp.connected) return;
      this.stomp.publish({
        destination: '/app/chat.seen',
        body: JSON.stringify({ roomId: this.session.roomId })
      });
    },

    async uploadFile(file) {
      if (!file || !this.session) return;
      const fd = new FormData();
      fd.append('file', file);
      try {
        const token = global.TokenStore && global.TokenStore.get();
        const headers = token ? { Authorization: 'Bearer ' + token } : {};
        const uploadPath = '/api/rtc/chat/rooms/' + this.session.roomId + '/upload';
        const uploadUrl = (this.api === global.AgentAPI && global.AgentAPI.base)
          ? (global.AgentAPI.base + uploadPath)
          : (this.ctx + uploadPath);
        const res = await fetch(uploadUrl, {
          method: 'POST', credentials: 'same-origin', headers: headers, body: fd
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Upload failed');
        this.appendMessage(this.normalizeMessage(data.data));
      } catch (err) {
        this.toast(err.message || 'Upload failed', 'error');
      }
      document.getElementById('rtcFileInput').value = '';
    },

    setReply(id, text) {
      this.replyToId = id;
      const bar = document.getElementById('rtcReplyBar');
      bar.classList.remove('rtc-hidden');
      document.getElementById('rtcReplyText').textContent = 'Reply: ' + (text || '').substring(0, 80);
    },

    clearReply() {
      this.replyToId = null;
      document.getElementById('rtcReplyBar').classList.add('rtc-hidden');
    },

    handleMeta(payload) {
      if (payload.type === 'DELETED') {
        const el = document.querySelector('[data-message-id="' + payload.messageId + '"]');
        if (el) el.remove();
      }
    },

    onPresence(data) {
      const key = (data.participantType || '') + '-' + (data.participantId || '');
      document.querySelectorAll('[data-rtc-presence="' + key + '"]').forEach(function (el) {
        EstateRTC.applyPresenceEl(el, data.status);
      });
      if (this.session) {
        const peerType = String(this.session.peerType || '');
        const peerId = String(this.session.peerId || '');
        if (peerType === String(data.participantType) && peerId === String(data.participantId)) {
          this.updatePeerPresence(data.status);
          const typingBar = document.getElementById('rtcTypingBar');
          if (data.typing && String(data.roomId) === String(this.session.roomId)) {
            typingBar.classList.remove('rtc-hidden');
          } else {
            typingBar.classList.add('rtc-hidden');
          }
        }
      }
    },

    updatePeerPresence(status) {
      const dot = document.getElementById('rtcPeerDot');
      if (!dot) return;
      const s = (status || 'OFFLINE').toString().toLowerCase().replace(/_/g, '_');
      dot.className = 'rtc-status-dot rtc-status-dot--' + s;
    },

    async refreshPresence(type, id, el) {
      try {
        const res = await this.request('/api/rtc/presence?type=' + encodeURIComponent(type) + '&id=' + id);
        this.applyPresenceEl(el, res.data.status);
      } catch (e) { /* ignore */ }
    },

    applyPresenceEl(el, status) {
      const dot = el.querySelector('.rtc-status-dot');
      const label = el.querySelector('.rtc-presence-label');
      const s = (status || 'OFFLINE').toUpperCase();
      if (dot) dot.className = 'rtc-status-dot rtc-status-dot--' + s.toLowerCase();
      if (label) label.textContent = s === 'TYPING' ? 'Typing…' : s.charAt(0) + s.slice(1).toLowerCase().replace('_', ' ');
    },

    async startVoiceCall(opts) {
      const self = this;
      try {
        if (!this.session || !this.session.roomId) {
          await this.ensureRoomSession(opts);
        }
      } catch (err) {
        this.toast(err.message || 'Could not start call', 'error');
        return;
      }
      if (!this.session || !this.session.roomId) return;
      await this.whenStompReady();
      if (!this.stomp || !this.stomp.connected) {
        this.toast('Not connected. Please wait or refresh.');
        return;
      }
      try {
        await this.ensureMic();
      } catch (err) {
        this.toast('Microphone permission required', 'error');
        return;
      }
      const sessionId = 'call-' + Date.now();
      this.callSession = { sessionId: sessionId, outgoing: true, peerName: opts.peerName };
      try {
        this.stomp.publish({
          destination: '/app/call.initiate',
          body: JSON.stringify({
            sessionId: sessionId,
            roomId: this.session.roomId,
            enquiryId: opts.enquiryId ? Number(opts.enquiryId) : null,
            bookingId: opts.bookingId ? Number(opts.bookingId) : null,
            pgBookingId: opts.pgBookingId ? Number(opts.pgBookingId) : null
          }),
          headers: this.stompConnectHeaders()
        });
      } catch (err) {
        this.toast(err.message || 'Could not start call', 'error');
        return;
      }
      this.showActiveCall(opts.peerName || 'Calling…');
      this.playRingtone(true);
    },

    onCallSignal(signal) {
      const self = this;
      const type = signal.type;
      if (type === 'INCOMING') {
        if (signal.sessionId && this._lastIncomingSession === signal.sessionId) {
          return;
        }
        if (signal.sessionId) this._lastIncomingSession = signal.sessionId;
        this.callSession = signal;
        this.showIncoming(signal.callerName || 'Incoming call');
        this.playRingtone(true);
        return;
      }
      if (signal.type === 'offer' || (signal.sdp && signal.type === 'offer')) {
        this.handleOffer(signal);
      } else if (signal.candidate) {
        this.addIceCandidate(signal.candidate);
      } else if (signal.type === 'answer' || (signal.sdp && signal.type === 'answer')) {
        this.handleAnswer(signal);
      } else if (type === 'ACCEPTED') {
        if (this.callSession && this.callSession.outgoing) {
          this.createPeerConnection(false).catch(function (err) {
            self.toast(err.message || 'Call failed', 'error');
          });
        }
        this.playRingtone(false);
      } else if (type === 'REJECTED' || type === 'COMPLETED' || type === 'MISSED' || type === 'CANCELLED') {
        if (type === 'REJECTED' || type === 'MISSED' || type === 'CANCELLED') {
          self.toast('Call ' + type.toLowerCase(), 'info');
        }
        this.cleanupCall();
      }
    },

    showIncoming(name) {
      document.getElementById('rtcIncomingCall').classList.remove('rtc-hidden');
      document.getElementById('rtcIncomingName').textContent = name;
      const sub = document.getElementById('rtcIncomingSub');
      if (sub) sub.textContent = 'Incoming voice call…';
    },

    hideIncoming() {
      document.getElementById('rtcIncomingCall').classList.add('rtc-hidden');
      this.playRingtone(false);
    },

    async acceptCall() {
      this.hideIncoming();
      try {
        await this.ensureMic();
        await this.whenStompReady();
        this.stomp.publish({
          destination: '/app/call.status',
          body: JSON.stringify({ sessionId: this.callSession.sessionId, status: 'ACCEPTED' })
        });
        this.showActiveCall(this.callSession.callerName || 'On call');
      } catch (err) {
        this.toast(err.message || 'Could not accept call', 'error');
        this.rejectCall();
      }
    },

    rejectCall() {
      this.hideIncoming();
      if (this.callSession && this.stomp && this.stomp.connected) {
        this.stomp.publish({
          destination: '/app/call.status',
          body: JSON.stringify({ sessionId: this.callSession.sessionId, status: 'REJECTED' })
        });
      }
      this.cleanupCall();
    },

    endCall(status) {
      if (this.callSession && this.stomp && this.stomp.connected) {
        this.stomp.publish({
          destination: '/app/call.status',
          body: JSON.stringify({ sessionId: this.callSession.sessionId, status: status || 'COMPLETED' })
        });
      }
      this.cleanupCall();
    },

    cleanupCall() {
      this.hideIncoming();
      document.getElementById('rtcActiveCall').classList.add('rtc-hidden');
      this.playRingtone(false);
      this.stopCallTimer();
      if (this.peerConnection) {
        this.peerConnection.close();
        this.peerConnection = null;
      }
      if (this.localStream) {
        this.localStream.getTracks().forEach(function (t) { t.stop(); });
        this.localStream = null;
      }
      this.callSession = null;
    },

    showActiveCall(label) {
      document.getElementById('rtcActiveCall').classList.remove('rtc-hidden');
      document.getElementById('rtcCallPeerLabel').textContent = label || 'On call';
    },

    async ensureMic() {
      if (this.localStream) return this.localStream;
      this.localStream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
      return this.localStream;
    },

    async createPeerConnection(isCallee) {
      const self = this;
      const iceServers = (this.session && this.session.stunServers || ['stun:stun.l.google.com:19302'])
        .map(function (u) { return { urls: u }; });
      this.peerConnection = new RTCPeerConnection({ iceServers: iceServers });
      this.localStream.getTracks().forEach(function (t) {
        self.peerConnection.addTrack(t, self.localStream);
      });
      this.peerConnection.ontrack = function (ev) {
        const audio = document.getElementById('rtcRemoteAudio');
        if (audio) {
          audio.srcObject = ev.streams[0];
          audio.play().catch(function () {});
        }
      };
      this.peerConnection.onicecandidate = function (ev) {
        if (ev.candidate && self.callSession && self.stomp && self.stomp.connected) {
          self.stomp.publish({
            destination: '/app/call.signal',
            body: JSON.stringify({
              sessionId: self.callSession.sessionId,
              type: 'candidate',
              candidate: ev.candidate
            })
          });
        }
      };
      if (!isCallee) {
        const offer = await this.peerConnection.createOffer();
        await this.peerConnection.setLocalDescription(offer);
        this.stomp.publish({
          destination: '/app/call.signal',
          body: JSON.stringify({
            sessionId: this.callSession.sessionId,
            type: 'offer',
            sdp: offer.sdp
          })
        });
      }
    },

    async handleOffer(signal) {
      await this.ensureMic();
      await this.createPeerConnection(true);
      await this.peerConnection.setRemoteDescription({ type: 'offer', sdp: signal.sdp });
      const answer = await this.peerConnection.createAnswer();
      await this.peerConnection.setLocalDescription(answer);
      this.stomp.publish({
        destination: '/app/call.signal',
        body: JSON.stringify({
          sessionId: signal.sessionId || this.callSession.sessionId,
          type: 'answer',
          sdp: answer.sdp
        })
      });
      this.showActiveCall('On call');
      this.startCallTimer();
      this.playRingtone(false);
    },

    async handleAnswer(signal) {
      if (!this.peerConnection) return;
      await this.peerConnection.setRemoteDescription({ type: 'answer', sdp: signal.sdp });
      this.playRingtone(false);
      this.startCallTimer();
    },

    async addIceCandidate(candidate) {
      if (!this.peerConnection || !candidate) return;
      try {
        await this.peerConnection.addIceCandidate(candidate);
      } catch (e) { console.warn('ICE', e); }
    },

    toggleMute() {
      this.muted = !this.muted;
      if (this.localStream) {
        this.localStream.getAudioTracks().forEach(function (t) { t.enabled = !EstateRTC.muted; });
      }
      document.getElementById('rtcMuteBtn').classList.toggle('active', this.muted);
    },

    toggleHold() {
      this.onHold = !this.onHold;
      if (this.localStream) {
        this.localStream.getAudioTracks().forEach(function (t) { t.enabled = !EstateRTC.onHold; });
      }
      document.getElementById('rtcHoldBtn').classList.toggle('active', this.onHold);
    },

    startCallTimer() {
      const start = Date.now();
      this.stopCallTimer();
      this.callTimerId = setInterval(function () {
        const sec = Math.floor((Date.now() - start) / 1000);
        const m = String(Math.floor(sec / 60)).padStart(2, '0');
        const s = String(sec % 60).padStart(2, '0');
        const el = document.getElementById('rtcCallTimer');
        if (el) el.textContent = m + ':' + s;
      }, 1000);
    },

    stopCallTimer() {
      if (this.callTimerId) clearInterval(this.callTimerId);
      this.callTimerId = null;
    },

    ensureAudioContext() {
      const AC = global.AudioContext || global.webkitAudioContext;
      if (!AC) return null;
      if (!this._ringCtx || this._ringCtx.state === 'closed') {
        this._ringCtx = new AC();
      }
      if (this._ringCtx.state === 'suspended') {
        this._ringCtx.resume().catch(function () {});
      }
      return this._ringCtx;
    },

    playRingtone(on) {
      if (!this.soundsEnabled) return;
      if (!on) {
        this.stopRingtone();
        return;
      }
      const ctx = this.ensureAudioContext();
      if (!ctx) return;
      this.stopRingtone();
      const self = this;

      function ringBurst() {
        if (!self._ringCtx || self._ringCtx.state === 'closed') return;
        [440, 480].forEach(function (freq, i) {
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.type = 'sine';
          osc.frequency.value = freq;
          gain.gain.value = 0.22;
          osc.connect(gain);
          gain.connect(ctx.destination);
          const t = ctx.currentTime + i * 0.45;
          osc.start(t);
          osc.stop(t + 0.4);
        });
      }

      ringBurst();
      this._ringInterval = setInterval(ringBurst, 2000);
    },

    stopRingtone() {
      if (this._ringInterval) {
        clearInterval(this._ringInterval);
        this._ringInterval = null;
      }
    },

    playMessageBeep() {
      if (!this.soundsEnabled) return;
      const ctx = this.ensureAudioContext();
      if (!ctx) return;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.frequency.value = 880;
      gain.gain.value = 0.08;
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.12);
    },

    async refreshUnreadSummary() {
      const self = this;
      try {
        const res = await this.request('/api/rtc/chat/unread-summary');
        self.applyUnreadSummary(res.data || {});
      } catch (e) {
        /* not signed in or RTC unavailable */
      }
    },

    applyUnreadSummary(data) {
      this.unread.total = Number(data.total) || 0;
      this.unread.enquiries = data.enquiries || {};
      this.unread.bookings = data.bookings || {};
      this.unread.pgBookings = data.pgBookings || {};
      this.renderAllUnreadBadges();
    },

    applyUnreadFromNotification(data) {
      if (data.unreadSummary) {
        this.applyUnreadSummary(data.unreadSummary);
        return;
      }
      if (data.enquiryId != null) {
        const k = String(data.enquiryId);
        if (data.roomUnread != null) {
          this.unread.enquiries[k] = Number(data.roomUnread);
        } else {
          this.unread.enquiries[k] = (Number(this.unread.enquiries[k]) || 0) + 1;
        }
      }
      if (data.bookingId != null) {
        const k = String(data.bookingId);
        if (data.roomUnread != null) {
          this.unread.bookings[k] = Number(data.roomUnread);
        } else {
          this.unread.bookings[k] = (Number(this.unread.bookings[k]) || 0) + 1;
        }
      }
      if (data.pgBookingId != null) {
        const k = String(data.pgBookingId);
        if (data.roomUnread != null) {
          this.unread.pgBookings[k] = Number(data.roomUnread);
        } else {
          this.unread.pgBookings[k] = (Number(this.unread.pgBookings[k]) || 0) + 1;
        }
      }
      if (data.totalUnread != null) {
        this.unread.total = Number(data.totalUnread);
      } else {
        this.recalcUnreadTotal();
      }
      this.renderAllUnreadBadges();
    },

    recalcUnreadTotal() {
      let total = 0;
      Object.keys(this.unread.enquiries).forEach(function (k) {
        total += Number(this.unread.enquiries[k]) || 0;
      }, this);
      Object.keys(this.unread.bookings).forEach(function (k) {
        total += Number(this.unread.bookings[k]) || 0;
      }, this);
      Object.keys(this.unread.pgBookings).forEach(function (k) {
        total += Number(this.unread.pgBookings[k]) || 0;
      }, this);
      this.unread.total = total;
    },

    clearUnreadForSession() {
      if (!this.session) return;
      if (this.session.enquiryId != null) {
        delete this.unread.enquiries[String(this.session.enquiryId)];
      }
      if (this.session.bookingId != null) {
        delete this.unread.bookings[String(this.session.bookingId)];
      }
      if (this.session.pgBookingId != null) {
        delete this.unread.pgBookings[String(this.session.pgBookingId)];
      }
      this.recalcUnreadTotal();
      this.renderAllUnreadBadges();
    },

    bumpUnreadForRoom(roomId, delta) {
      if (this.session && String(this.session.roomId) === String(roomId)) {
        if (this.session.enquiryId != null) {
          const k = String(this.session.enquiryId);
          this.unread.enquiries[k] = (Number(this.unread.enquiries[k]) || 0) + delta;
        }
        if (this.session.bookingId != null) {
          const k = String(this.session.bookingId);
          this.unread.bookings[k] = (Number(this.unread.bookings[k]) || 0) + delta;
        }
        if (this.session.pgBookingId != null) {
          const k = String(this.session.pgBookingId);
          this.unread.pgBookings[k] = (Number(this.unread.pgBookings[k]) || 0) + delta;
        }
        this.recalcUnreadTotal();
        this.renderAllUnreadBadges();
      }
    },

    setBadgeEl(el, count) {
      const n = Number(count) || 0;
      if (n > 0) {
        el.textContent = n > 99 ? '99+' : String(n);
        el.classList.remove('rtc-hidden');
        el.setAttribute('aria-label', n + ' unread');
      } else {
        el.textContent = '';
        el.classList.add('rtc-hidden');
        el.removeAttribute('aria-label');
      }
    },

    sumMap(map) {
      let s = 0;
      Object.keys(map || {}).forEach(function (k) { s += Number(map[k]) || 0; });
      return s;
    },

    renderAllUnreadBadges() {
      const self = this;
      document.querySelectorAll('[data-rtc-badge-key]').forEach(function (el) {
        const key = el.getAttribute('data-rtc-badge-key') || '';
        let count = 0;
        if (key.indexOf('enquiry-') === 0) {
          count = self.unread.enquiries[key.substring(8)] || 0;
        } else if (key.indexOf('pg-booking-') === 0) {
          count = self.unread.pgBookings[key.substring(11)] || 0;
        } else if (key.indexOf('booking-') === 0) {
          count = self.unread.bookings[key.substring(8)] || 0;
        }
        self.setBadgeEl(el, count);
      });

      const enqTotal = this.sumMap(this.unread.enquiries);
      const bookTotal = this.sumMap(this.unread.bookings);
      document.querySelectorAll('[data-rtc-nav-badge="enquiries"]').forEach(function (el) {
        self.setBadgeEl(el, enqTotal);
      });
      document.querySelectorAll('[data-rtc-nav-badge="bookings"]').forEach(function (el) {
        self.setBadgeEl(el, bookTotal);
      });
      document.querySelectorAll('[data-rtc-nav-badge="messages"]').forEach(function (el) {
        self.setBadgeEl(el, self.unread.total);
      });
      document.querySelectorAll('[data-rtc-nav-badge="chat-total"]').forEach(function (el) {
        self.setBadgeEl(el, self.unread.total);
      });

      if (this._baseDocumentTitle) {
        document.title = this.unread.total > 0
          ? '(' + this.unread.total + ') ' + this._baseDocumentTitle
          : this._baseDocumentTitle;
      }
    },

    notify(title, body) {
      this.toast(title + ': ' + (body || '').substring(0, 60));
      if ('Notification' in global && Notification.permission === 'granted') {
        try {
          new Notification(title, { body: body });
        } catch (e) { /* ignore */ }
      }
    },

    onNotification(data) {
      if (data.type === 'NEW_MESSAGE' && data.message) {
        const m = this.normalizeMessage(data.message);
        const panel = document.getElementById('rtcChatPanel');
        const chatOpen = panel && !panel.classList.contains('rtc-hidden');
        const sameRoom = this.session && String(data.roomId) === String(this.session.roomId);
        if (sameRoom && chatOpen) {
          this.appendMessage(m);
          if (!m.mine) {
            this.playMessageBeep();
            this.markSeen();
            this.clearUnreadForSession();
          }
        } else if (!m.mine) {
          this.playMessageBeep();
          this.notify('New message', m.message);
          this.applyUnreadFromNotification(data);
        }
      }
    },

    toast(msg) {
      const stack = document.getElementById('rtcToastStack');
      if (stack) {
        const t = document.createElement('div');
        t.className = 'rtc-toast';
        t.textContent = msg;
        stack.appendChild(t);
        setTimeout(function () { t.remove(); }, 4500);
      }
      if (global.UI && global.UI.toast) global.UI.toast(msg, 'info');
    }
  };

  function esc(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function escAttr(s) { return esc(s).replace(/"/g, '&quot;'); }

  function formatTime(iso) {
    if (!iso) return '';
    try {
      const d = new Date(iso);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch (e) { return ''; }
  }

  global.EstateRTC = EstateRTC;
})(typeof window !== 'undefined' ? window : this);
