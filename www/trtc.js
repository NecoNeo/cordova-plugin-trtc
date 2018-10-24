var exec = require('cordova/exec');

function Trtc() {}

Trtc.prototype.hello = function() {
    window.alert('h');
    console.log('hola');
};

Trtc.prototype.joinChannel = function(channelId, uid) {
    // exec(function(result) {}, function(err) {}, 'Agora', 'joinChannel', [channelId, uid]);
};

module.exports = new Trtc();
