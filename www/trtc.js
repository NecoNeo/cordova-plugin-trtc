var exec = require('cordova/exec');

function Trtc() {}

Trtc.prototype.joinChannel = function(params) {
    // not to be implemented
};

Trtc.prototype.showCreatePage = function() {
    exec(function(result) {}, function(err) {}, 'Trtc', 'showCreatePage', []);
}

module.exports = new Trtc();
