$("document").ready(function(){
    $("#save-playlist-btn").on("click", function(e){
        e.preventDefault();
        e.stopImmediatePropagation();

        console.log("save-playlist-btn OnClick()");
        SavePlaylist(e);
    })

    $("#load-playlist-btn").on("click", function(e) {
      e.preventDefault();
      e.stopImmediatePropagation();

      console.log("load-playlist-btn OnClick()");
      LoadPlaylist();
    })

    $("#song-list").contextmenu(function(e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        
        let element = e.target;
        addSongToPlaylist(element);
      });
});

function addSongToPlaylist(songElement) {
    let playList = document.getElementById("play-list");
  
    //Trying to create new entry by copying existing one
    // let exampleRow = document.getElementsByClassName("play-list-row");
    // let trackRow = exampleRow[0].cloneNode(true);

    //HTML template testing
    let template = document.getElementById("track-row-template");
    let trackRow = template.content.firstElementChild.cloneNode(true);
  
    playList.appendChild(trackRow);
  
    trackRow.querySelector(".playlist-track").innerHTML = songElement.innerHTML;
    trackRow.querySelector(".playlist-track").setAttribute("data-track-ID", songElement.getAttribute("songid"));
    trackRow.getElementsByClassName("track-number")[0].innerHTML = _elements.playListRows.length + ".";
    trackRow.setAttribute("data-track-row", _elements.playListRows.length);
  
    //create new source for audio element
    let source = document.createElement("source");
    source.setAttribute("data-track-number", _elements.playListRows.length);
    source.src = "https://" + location.host + "/MediaServer/song?id=" + songElement.getAttribute("songid");
  
    document.getElementById("audio").appendChild(source);
  
    //Add event listener to playlist entry
    // let smallToggleBtn = document.getElementsByClassName("small-toggle-btn");
    //let smallToggleBtn = _elements.playerButtons.smallToggleBtn[i];
    let playListLink = trackRow.children[2].children[0];
    
    //Playlist link clicked
    playListLink.addEventListener("click", function(e) {
      e.preventDefault();
      let selectedTrack = parseInt(this.parentNode.parentNode.getAttribute("data-track-row"));
      
      if (selectedTrack !== _currentTrack) {
        _resetPlayStatus();
        _currentTrack = null;
        _trackLoaded = false;
      }
      
      if(_trackLoaded === false) {
        _currentTrack = parseInt(selectedTrack);
        _setTrack();
      } else {
        _playBack(this);
      }
    }, false);
}

function addAlbumToPlaylist(albumElement) {
  albumID = albumElement.parentNode.getAttribute("albumid");

  let playList = document.getElementById("play-list");

  $.getJSON("/MediaServer/json?albumid=" + albumID, function(data) {
    $.each( data, function( key, value ) {
      //Trying to create new entry by copying existing one
      // let exampleRow = document.getElementsByClassName("play-list-row");
      // let trackRow = exampleRow[0].cloneNode(true);

      //HTML template testing
      let template = document.getElementById("track-row-template");
      let trackRow = template.content.firstElementChild.cloneNode(true);
    
      playList.appendChild(trackRow);
    
      trackRow.querySelector(".playlist-track").innerHTML = value.song_name;
      trackRow.querySelector(".playlist-track").setAttribute("data-track-id", value.song_id);
      trackRow.getElementsByClassName("track-number")[0].innerHTML = _elements.playListRows.length + ".";
      trackRow.setAttribute("data-track-row", _elements.playListRows.length);
    
      //create new source for audio element
      let source = document.createElement("source");
      source.setAttribute("data-track-number", _elements.playListRows.length);
      source.src = "https://" + location.host + "/MediaServer/song?id=" + value.song_id;
    
      document.getElementById("audio").appendChild(source);
    
      //Add event listener to playlist entry
      // let smallToggleBtn = document.getElementsByClassName("small-toggle-btn");
      //let smallToggleBtn = _elements.playerButtons.smallToggleBtn[i];
      let playListLink = trackRow.children[2].children[0];
      
      //Playlist link clicked
      playListLink.addEventListener("click", function(e) {
        e.preventDefault();
        let selectedTrack = parseInt(this.parentNode.parentNode.getAttribute("data-track-row"));
        
        if (selectedTrack !== _currentTrack) {
          _resetPlayStatus();
          _currentTrack = null;
          _trackLoaded = false;
        }
        
        if(_trackLoaded === false) {
          _currentTrack = parseInt(selectedTrack);
          _setTrack();
        } else {
          _playBack(this);
        }
      }, false);
    });
  })
}

// Save playlist to the server
//TODO choose a playlist to save instead of just default one
//Currently just saves over one default list
function SavePlaylist() {
  let playlistJSON = new Array();
  playlistJSON.push({"playlistName" : "Default"});
  playlistJSON.push({"playlistID" : ""});
  let playlistTracks = new Array();
  $(".play-list-row").each(function(index) {
    // console.log("Index: " + index + ", TrackNr: " + $(this).attr("data-track-row") + ", Track name: " + $(this).find(".playlist-track").html());
    playlistTracks.push({"trackNr" : $(this).attr("data-track-row"),
     "trackName" : $(this).find(".playlist-track").html(),
      "trackID" : $(this).find(".playlist-track").attr("data-track-id")});
  })
  playlistJSON.push({"playlistTracks" : playlistTracks});
  console.log(playlistJSON);

  $.ajax({
    'method':"POST",
    'url':"/MediaServer/playlist?playlistid=" + "",
    'data':JSON.stringify(playlistJSON),
    'dataType':'json',
    'processData':false,
    'contentType':'application/json',
    'jsonp':false
  });
}

function LoadPlaylist() {

}