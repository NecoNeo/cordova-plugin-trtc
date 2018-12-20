var exec = require('cordova/exec');

function Trtc() {}

Trtc.prototype.joinChannel = function(channelId, uid) {
    // exec(function(result) {}, function(err) {}, 'Agora', 'joinChannel', [channelId, uid]);
};

Trtc.prototype.showCreateActivity = function() {
    exec(function(result) {}, function(err) {}, 'Trtc', 'showCreateActivity', []);
}

module.exports = new Trtc();
