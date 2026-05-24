/**
 * Agent property enquiries — accept, decline, reply.
 */
(function (global) {
  'use strict';

  const AgentEnquiries = {
    ctx: '',

    init(ctx) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.load();
    },

    async load() {
      const listEl = document.getElementById('enquiriesList');
      const empty = document.getElementById('enquiriesEmpty');
      if (!listEl) return;
      UI.showLoader();
      try {
        const data = await AgentAPI.request(this.ctx + '/api/agent/enquiries');
        const list = data.data || [];
        if (!list.length) {
          listEl.innerHTML = '';
          if (empty) empty.style.display = 'block';
          return;
        }
        if (empty) empty.style.display = 'none';
        listEl.innerHTML = list.map(function (e) { return AgentEnquiries.cardHtml(e); }).join('');
        this.bindActions(listEl);
        if (global.EstateRTC) {
          EstateRTC.bindCardActions(listEl);
        }
      } catch (err) {
        UI.toast(err.message || 'Could not load enquiries', 'error');
      } finally {
        UI.hideLoader();
      }
    },

    bindActions(listEl) {
      const self = this;
      listEl.querySelectorAll('[data-action]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          const action = btn.getAttribute('data-action');
          const id = btn.getAttribute('data-id');
          if (!id) return;
          if (action === 'accept') self.accept(id);
          else if (action === 'decline') self.decline(id);
          else if (action === 'reply') self.promptReply(id);
        });
      });
    },

    async accept(id) {
      try {
        const data = await AgentAPI.request(this.ctx + '/api/agent/enquiries/' + id + '/accept', { method: 'PATCH' });
        UI.toast(data.message || 'Accepted', 'success');
        this.load();
      } catch (err) {
        UI.toast(err.message || 'Failed', 'error');
      }
    },

    async decline(id) {
      const ok = typeof Swal !== 'undefined'
        ? (await Swal.fire({
          title: 'Decline enquiry?',
          text: 'The buyer will be notified this enquiry was declined.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#d4af37',
          confirmButtonText: 'Decline'
        })).isConfirmed
        : window.confirm('Decline this enquiry?');
      if (!ok) return;
      try {
        const data = await AgentAPI.request(this.ctx + '/api/agent/enquiries/' + id + '/decline', { method: 'PATCH' });
        UI.toast(data.message || 'Declined', 'success');
        this.load();
      } catch (err) {
        UI.toast(err.message || 'Failed', 'error');
      }
    },

    promptReply(id) {
      const self = this;
      if (typeof Swal === 'undefined') {
        const reply = window.prompt('Enter your reply to the buyer (min 10 characters):');
        if (reply) self.sendReply(id, reply);
        return;
      }
      Swal.fire({
        title: 'Reply to buyer',
        input: 'textarea',
        inputPlaceholder: 'Your detailed reply…',
        inputAttributes: { minlength: 10, maxlength: 2000 },
        showCancelButton: true,
        confirmButtonColor: '#d4af37',
        confirmButtonText: 'Send reply',
        preConfirm: function (value) {
          if (!value || value.trim().length < 10) {
            Swal.showValidationMessage('Reply must be at least 10 characters');
            return false;
          }
          return value.trim();
        }
      }).then(function (result) {
        if (result.isConfirmed && result.value) {
          self.sendReply(id, result.value);
        }
      });
    },

    async sendReply(id, reply) {
      try {
        const data = await AgentAPI.request(this.ctx + '/api/agent/enquiries/' + id + '/reply', {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ reply: reply })
        });
        UI.toast(data.message || 'Reply sent', 'success');
        this.load();
      } catch (err) {
        UI.toast(err.message || 'Failed to send reply', 'error');
      }
    },

    cardHtml(e) {
      const status = (e.status || '').toLowerCase();
      let actions = '';
      if (e.status === 'PENDING') {
        actions = '<div class="agent-enquiry-actions">' +
          '<button type="button" class="agent-btn-gold" data-action="accept" data-id="' + e.id + '">Accept</button>' +
          '<button type="button" class="agent-btn-outline" data-action="decline" data-id="' + e.id + '">Decline</button>' +
          '</div>';
      } else if (e.status === 'ACCEPTED') {
        actions = '<div class="agent-enquiry-actions">' +
          '<button type="button" class="agent-btn-gold" data-action="reply" data-id="' + e.id + '">Send reply</button>' +
          '</div>';
      }
      const replyBlock = e.agentReply
        ? '<div class="agent-enquiry-reply"><strong>Your reply</strong><p>' + esc(e.agentReply) + '</p></div>'
        : '';
      return '<article class="agent-enquiry-card agent-enquiry-card--' + status + '">' +
        '<div class="agent-enquiry-head">' +
        '<div><h4>' + esc(e.propertyTitle || 'Property') + '</h4>' +
        '<span class="agent-enquiry-code">' + esc(e.propertyCode || '') + '</span></div>' +
        '<span class="agent-enquiry-status agent-enquiry-status--' + status + '">' + esc(e.statusLabel || e.status) + '</span></div>' +
        '<p class="agent-enquiry-buyer"><strong>' + esc(e.userName) + '</strong> · ' + esc(e.userEmail) +
        (e.userMobile ? ' · ' + esc(e.userMobile) : '') + '</p>' +
        '<p class="agent-enquiry-msg">' + esc(e.message) + '</p>' +
        replyBlock + actions +
        (global.EstateRTC ? EstateRTC.cardActionsHtml({
          enquiryId: e.id,
          peerId: e.userId,
          peerName: e.userName,
          propertyTitle: e.propertyTitle
        }) : '') +
        '</article>';
    }
  };

  function esc(s) {
    if (!s) return '';
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  global.AgentEnquiries = AgentEnquiries;
})(typeof window !== 'undefined' ? window : this);
