/**
 * PG booking form — room, beds, guest details.
 */
(function (global) {
  'use strict';

  const PgBookingForm = {
    ctx: '',
    pgId: null,
    rooms: [],
    selectedRoom: null,

    init(ctx, options) {
      this.ctx = (ctx || '').replace(/\/$/, '');
      this.pgId = options && options.pgId;
      this.bindForm();
      this.loadRooms();
    },

    bindForm() {
      const self = this;
      document.getElementById('roomSelect')?.addEventListener('change', function () {
        self.onRoomChange(this.value);
      });
      document.getElementById('bedCount')?.addEventListener('change', function () {
        self.renderBedChips();
        self.renderOccupants();
      });
      document.getElementById('pgBookingForm')?.addEventListener('submit', function (e) {
        e.preventDefault();
        self.submitBooking();
      });
    },

    async loadRooms() {
      const loader = document.getElementById('pgBookingFormLoader');
      const form = document.getElementById('pgBookingForm');
      const errEl = document.getElementById('pgBookingFormError');
      if (loader) loader.style.display = 'flex';
      try {
        const data = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/rooms?pgId=' + this.pgId);
        this.rooms = data.data || [];
        if (!this.rooms.length) {
          throw new Error('No rooms with available beds right now.');
        }
        this.renderRoomSelect();
        if (form) form.style.display = 'block';
      } catch (err) {
        if (errEl) {
          errEl.style.display = 'block';
          errEl.innerHTML = '<p>' + esc(err.message) + '</p><a href="' + esc(this.ctx) + '/user/pgs/' + this.pgId + '/book" class="btn-gold">Back</a>';
        }
      } finally {
        if (loader) loader.style.display = 'none';
      }
    },

    renderRoomSelect() {
      const sel = document.getElementById('roomSelect');
      if (!sel) return;
      sel.innerHTML = this.rooms.map(function (r) {
        var label = 'Room ' + r.roomNumber;
        if (r.floorNumber) label += ' (Floor ' + r.floorNumber + ')';
        label += ' — ' + (r.availableBeds || 0) + ' bed(s) free';
        return '<option value="' + r.roomId + '">' + esc(label) + '</option>';
      }).join('');
      this.onRoomChange(sel.value);
    },

    onRoomChange(roomId) {
      this.selectedRoom = this.rooms.find(function (r) { return String(r.roomId) === String(roomId); }) || null;
      const hint = document.getElementById('roomHint');
      const bedCount = document.getElementById('bedCount');
      if (this.selectedRoom && hint) {
        hint.textContent = 'Sharing: ' + (this.selectedRoom.sharingType || '—') +
          ' · Rent: ' + formatInr(this.selectedRoom.rentPerBed) + '/bed/mo';
      }
      if (this.selectedRoom && bedCount) {
        bedCount.max = this.selectedRoom.availableBeds || 1;
        if (parseInt(bedCount.value, 10) > (this.selectedRoom.availableBeds || 1)) {
          bedCount.value = this.selectedRoom.availableBeds || 1;
        }
      }
      this.renderBedChips();
      this.renderOccupants();
    },

    renderBedChips() {
      const wrap = document.getElementById('bedNumbersWrap');
      if (!wrap || !this.selectedRoom) return;
      const beds = this.selectedRoom.availableBedNumbers || [];
      wrap.innerHTML = beds.map(function (b) {
        return '<label class="pg-bed-chip"><input type="checkbox" name="bedNumber" value="' + b + '"> Bed ' + b + '</label>';
      }).join('');
      wrap.querySelectorAll('input[type=checkbox]').forEach(function (cb) {
        cb.addEventListener('change', function () { PgBookingForm.enforceBedLimit(); });
      });
    },

    enforceBedLimit() {
      const max = parseInt(document.getElementById('bedCount')?.value, 10) || 1;
      const checked = document.querySelectorAll('#bedNumbersWrap input:checked');
      if (checked.length > max) {
        checked[checked.length - 1].checked = false;
      }
      if (checked.length === max) {
        document.querySelectorAll('#bedNumbersWrap input:not(:checked)').forEach(function (cb) {
          cb.disabled = true;
        });
      } else {
        document.querySelectorAll('#bedNumbersWrap input').forEach(function (cb) { cb.disabled = false; });
      }
    },

    renderOccupants() {
      const list = document.getElementById('occupantsList');
      const count = parseInt(document.getElementById('bedCount')?.value, 10) || 1;
      if (!list) return;
      let html = '';
      for (var i = 0; i < count; i++) {
        html += '<div class="pg-occupant-card">' +
          '<h4>Guest ' + (i + 1) + '</h4>' +
          '<label>Full name<input type="text" class="occ-name" required minlength="2" placeholder="Guest full name"></label>' +
          '<label>Mobile<input type="tel" class="occ-mobile" required minlength="10" placeholder="10-digit mobile"></label>' +
          '<label>Email (optional)<input type="email" class="occ-email" placeholder="email@example.com"></label>' +
          '</div>';
      }
      list.innerHTML = html;
    },

    getSelectedBedNumbers() {
      return Array.from(document.querySelectorAll('#bedNumbersWrap input:checked'))
        .map(function (cb) { return parseInt(cb.value, 10); })
        .sort(function (a, b) { return a - b; });
    },

    async submitBooking() {
      const bedCount = parseInt(document.getElementById('bedCount')?.value, 10) || 0;
      const bedNumbers = this.getSelectedBedNumbers();
      if (bedNumbers.length !== bedCount) {
        toast('Select exactly ' + bedCount + ' bed number(s)', 'error');
        return;
      }
      const cards = document.querySelectorAll('.pg-occupant-card');
      const occupants = [];
      cards.forEach(function (card, i) {
        occupants.push({
          fullName: card.querySelector('.occ-name')?.value?.trim(),
          mobile: card.querySelector('.occ-mobile')?.value?.trim(),
          email: card.querySelector('.occ-email')?.value?.trim() || null,
          bedNumber: bedNumbers[i] || null
        });
      });

      const btn = document.getElementById('confirmPgBookingBtn');
      if (btn) btn.disabled = true;
      try {
        const res = await AuthAPI.request(this.ctx + '/api/user/pg-bookings/draft', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            pgPropertyId: Number(this.pgId),
            roomId: Number(this.selectedRoom.roomId),
            bedCount: bedCount,
            bedNumbers: bedNumbers,
            occupants: occupants,
            paymentMethod: 'UPI'
          })
        });
        const bookingId = res.data && res.data.id;
        if (!bookingId) throw new Error('Could not save booking');
        window.location.href = this.ctx + '/user/pg-booking/payment?bookingId=' + bookingId;
      } catch (err) {
        toast(err.message || 'Could not save booking', 'error');
        if (btn) btn.disabled = false;
      }
    }
  };

  function formatInr(n) {
    var v = parseFloat(n);
    if (isNaN(v)) return '₹ —';
    return '₹ ' + v.toLocaleString('en-IN');
  }

  function esc(s) {
    return String(s || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function toast(msg, type) {
    if (typeof UI !== 'undefined' && UI.toast) UI.toast(msg, type);
    else alert(msg);
  }

  global.PgBookingForm = PgBookingForm;
})(typeof window !== 'undefined' ? window : this);
