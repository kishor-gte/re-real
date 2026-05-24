<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="${ctx}/css/rtc-chat.css?v=4">
<div id="rtcRoot" class="rtc-root" aria-live="polite">
    <div id="rtcChatPanel" class="rtc-chat-panel rtc-hidden" role="dialog" aria-label="Chat">
        <header class="rtc-chat-header">
            <div class="rtc-chat-peer">
                <span id="rtcPeerDot" class="rtc-status-dot rtc-status-dot--offline"></span>
                <div>
                    <strong id="rtcPeerName">Chat</strong>
                    <small id="rtcPeerSub">Property</small>
                </div>
            </div>
            <div class="rtc-chat-toolbar">
                <button type="button" id="rtcChatMin" class="rtc-icon-btn" title="Minimize">&#8722;</button>
                <button type="button" id="rtcChatClose" class="rtc-icon-btn" title="Close">&times;</button>
            </div>
        </header>
        <div id="rtcTypingBar" class="rtc-typing-bar rtc-hidden">Typing…</div>
        <div id="rtcMessages" class="rtc-messages"></div>
        <div id="rtcReplyBar" class="rtc-reply-bar rtc-hidden">
            <span id="rtcReplyText"></span>
            <button type="button" id="rtcReplyCancel" class="rtc-icon-btn">&times;</button>
        </div>
        <footer class="rtc-chat-footer">
            <label class="rtc-attach-btn" title="Attach file">
                <input type="file" id="rtcFileInput" accept="image/*,.pdf,audio/*" hidden>
                &#128206;
            </label>
            <input type="text" id="rtcMessageInput" class="rtc-message-input" placeholder="Type a message…" maxlength="4000" autocomplete="off">
            <button type="button" id="rtcSendBtn" class="rtc-send-btn">Send</button>
        </footer>
    </div>

    <div id="rtcIncomingCall" class="rtc-call-modal rtc-hidden">
        <div class="rtc-call-card rtc-glass">
            <div class="rtc-call-avatar" id="rtcCallAvatar">&#128222;</div>
            <h3 id="rtcIncomingName">Incoming call</h3>
            <p id="rtcIncomingSub">Voice call</p>
            <audio id="rtcRingtone" loop preload="none"></audio>
            <div class="rtc-call-actions">
                <button type="button" id="rtcRejectBtn" class="rtc-call-btn rtc-call-btn--reject">Reject</button>
                <button type="button" id="rtcAcceptBtn" class="rtc-call-btn rtc-call-btn--accept">Accept</button>
            </div>
        </div>
    </div>

    <div id="rtcActiveCall" class="rtc-call-bar rtc-hidden">
        <div class="rtc-call-bar-info">
            <span id="rtcCallTimer">00:00</span>
            <span id="rtcCallPeerLabel">On call</span>
            <span id="rtcNetworkDot" class="rtc-network-dot rtc-network-dot--ok" title="Network"></span>
        </div>
        <div class="rtc-call-bar-actions">
            <button type="button" id="rtcMuteBtn" class="rtc-call-mini" title="Mute">&#128263;</button>
            <button type="button" id="rtcHoldBtn" class="rtc-call-mini" title="Hold">&#9208;</button>
            <button type="button" id="rtcSpeakerBtn" class="rtc-call-mini" title="Speaker">&#128266;</button>
            <button type="button" id="rtcEndCallBtn" class="rtc-call-mini rtc-call-mini--end" title="End">&#128222;</button>
        </div>
        <audio id="rtcRemoteAudio" autoplay playsinline></audio>
    </div>

    <div id="rtcToastStack" class="rtc-toast-stack"></div>
</div>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>
<script src="${ctx}/js/estate-rtc.js?v=7"></script>
