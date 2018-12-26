var exec = require('cordova/exec');

function Trtc() {}

Trtc.prototype.joinChannel = function(channelId, uid) {
    // exec(function(result) {}, function(err) {}, 'Agora', 'joinChannel', [channelId, uid]);
};

Trtc.prototype.showCreatePage = function() {
    exec(function(result) {}, function(err) {}, 'Trtc', 'showCreatePage', []);
}

module.exports = new Trtc();
