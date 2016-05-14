
createPlaylistContainer = function (playlist){
    var playlistContainer = document.createElement("div");
    playlistContainer.id = "playlist-" + playlist.id;
    playlistContainer.style.outline = "1px solid #333333";
    var jsr = jsRouter.controllers.Playlists.play(playlist.id);

    playlistContainer.appendChild(
        createHyperLink(playlist.title, jsr.url));

    playlistContainer.appendChild(createTextDiv(playlist.size.toString().concat(" links")));
    return playlistContainer;
};


$(document).ready(function(){
    loadPL(
        jsRouter.controllers.json.PlaylistJSON.getUsrPublicList(getQueryVariable("username")).url,
        'public-pl');
});