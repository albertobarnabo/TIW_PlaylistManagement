(function() {// avoid variables ending up in the global scope

	var playlists, songs, createSong, createPlaylist, addSongToPlaylist, songDetails, dragAndDrop, genres, currentPlaylist, groupId, nextGroup,
		previousGroup, songslist, sort, saveSort
	    pageOrchestrator = new PageOrchestrator();
	
	//What happens when the page is loaded
	window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "LoginPage.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);
	  
	 //Contructors of view components
	 
	function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	  }
	  
	 function Playlists(_alert, _listcontainer, _listcontainerbody) {
	   this.alert = _alert;
	   this.listcontainer = _listcontainer;
	   this.listcontainerbody = _listcontainerbody;
	   
	   
	   this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetPlaylistsData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var playlistsToShow = JSON.parse(req.responseText);
	              if (playlistsToShow.length == 0) {
	                self.alert.textContent = "No playlists yet!";
	                currentPlaylist = -1;
	                return;
	              }
	              else{
	              	currentPlaylist = playlistsToShow[0].playlistId;
	              	
	              }
	              self.update(playlistsToShow); 
	              if (next) next(); // show the default element of the list if present
	            }
	          } else {
	            self.alert.textContent = message;
	          }
	        }
	      );
	    };
	    
	    this.update = function(playlistsArray) {
	      var elem, i, row, namecell, datecell, linkcell, anchor;
	  		
	      this.listcontainerbody.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      playlistsArray.forEach(function(playlist) { // self visible here, not this
	        row = document.createElement("tr");
	        row.className = "playlistnotselected";
	        
	        namecell = document.createElement("td");
	        namecell.textContent = playlist.playlistName;
	        row.appendChild(namecell);
	        datecell = document.createElement("td");
	        datecell.textContent = playlist.creationDate;
	        row.appendChild(datecell);
	        
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode("Show");
	        anchor.appendChild(linkText);
	        anchor.setAttribute('playlistid', playlist.playlistId); 
	        
	        anchor.addEventListener("click", (e) => {		        
		        currentPlaylist = e.target.getAttribute("playlistid");	          
		        songs.show(e.target.getAttribute("playlistid"));
		        orphanSongs.show();
		        var playlistscontainer = document.getElementById("id_playlistcontainerbody");
		        playlistscontainer.querySelectorAll("tr").forEach(function(row){
		        	row.className = "playlistnotselected";
		        });
		        e.target.closest("tr").className = "playlistselected";	
		        groupId=1;                  
	        }, false);
	        
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        self.listcontainerbody.appendChild(row);
	      });
	      
	      
	      var playlistscontainer = document.getElementById("id_playlistcontainerbody");
		  playlistscontainer.querySelectorAll("tr")[0].className = "playlistselected";
		  
	      this.listcontainer.style.visibility = "visible";
	      if(currentPlaylist != -1){
		  	songs.show(currentPlaylist);	
		  	orphanSongs.show();
		  	}
	    }
	    
	 }
	 
	 function NextGroup(){
        
         this.registerEvents = function(orchestrator) {   
        
            document.getElementById("id_nextbutton").addEventListener("click", (e) => {
                e.preventDefault();
                
                 groupId ++;
                 songs.update(songslist);
                   
              });
         };    
     }    
    
     function PreviousGroup(){
    
         this.registerEvents = function(orchestrator) {   
        
            document.getElementById("id_previousbutton").addEventListener("click", (e) => {
                e.preventDefault();
                groupId --;
                songs.update(songslist);
              });
         };
     }
	
	  
	 function Songs(options){
	 	
	 	this.songscontainer = options['songscontainer'];
	 	
	 	this.alert = options['alert'];
	 	this.nextbutton = options['nextbutton'];
	 	this.sortbutton = options['sortbutton'];
	 	this.previousbutton = options['previousbutton'];
	 	this.addsongtoplaylistform = options['addsongtoplaylistform'];
	 	this.labeladdsong = options['labeladdsong'];
	 	this.addsongoptions = options['addsongoptions'];
	 		 	
	 	
	 	this.show = function(playlistid) {
	 	
	      var self = this;
	      var title = document.getElementById("id_playlistTitle");
	      
	      this.nextbutton.style.visibility = "hidden";
          this.previousbutton.style.visibility = "hidden";
	      
	      makeCall("GET", "GetSongsData?playlistId=" + playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	            
	              songslist = JSON.parse(req.responseText);
	              
	              if (songs.length == 0) {
               		  self.alert.textContent = "No songs yet!";
	              }	
	              else{	
		              
	              }	              	              
	              self.update(songslist); 
	             
	              self.songscontainer.style.visibility = "visible";
	              	              
	            } else {
	              self.alert.textContent = message;

	            }
	          }
	        }
	      );
	    };
	    
	    this.update = function(songsArray) {
	      var elem, i, row, title, image, link, cell, name, p, j;
	  	  var self = this;
	  	  self.alert.textContent = "";	
          if(groupId > 1) {
              self.previousbutton.style.visibility = "visible";
          }
          else
          	   self.previousbutton.style.visibility = "hidden";
          if(songslist.length > 5*groupId) {
              self.nextbutton.style.visibility = "visible";
          }
          else
          	   self.nextbutton.style.visibility = "hidden";
	  	  
	      this.songscontainer.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      row = document.createElement("tr");
	      
	     
	      
	      songsArray.forEach(function(song, idx) { // self visible here, not this
	        if(idx >= (groupId-1)*5 && idx < groupId*5){
		        cell = document.createElement("th");
		        row.appendChild(cell);   
		           
		        title = document.createElement("a");
		        title.textContent = song.title;
		        cell.appendChild(title);
		        
		        p = document.createElement("p");
		        cell.appendChild(p);
		        	        
		        image = document.createElement("img");
		        var uri = encodeURI(song.imageFile);
		        image.src = "http://localhost:8080/TIW_PlaylistManagement/GetImage?path="+uri;
		        image.width = 150;
		        image.height = 150;	      
		        p.appendChild(image);
		        
		       	title.addEventListener("click", (e) => {	          
		          songDetails.show(song.songId); 	          
		        }, false);
		        
		        title.href = "#";
		        
		        self.songscontainer.appendChild(row);
	        }
	      });
	      this.songscontainer.style.visibility = "visible";

	    }
	    
	 }
	 
	 function CreateSong(_creationform){
	 
	 	this.creationform = _creationform;
	 	
	 	this.registerEvents = function(orchestrator) {	
	 	 	
			this.creationform.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			e.preventDefault();
			var eventfieldset = e.target.closest("fieldset"),
		    valid = true;
	        for (i = 0; i < eventfieldset.elements.length; i++) {
	          if (!eventfieldset.elements[i].checkValidity()) {
	            eventfieldset.elements[i].reportValidity();
	            valid = false;
	            break;
	          }
	        }
	
	        if (valid) {
	          var self = this;
	         
	          makeCall("POST", "CreateSong", e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message
	                if (req.status == 200) { 
	                  orphanSongs.show();
	                  songs.show(currentPlaylist);
	                } else {
	                  //self.alert.textContent = message;
	                  //self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
	     };
	 	
	 		 	
	 }
	 
	 function CreatePlaylist(_creationform){

	 	this.creationform = _creationform;
	 	
	 	this.registerEvents = function(orchestrator) {	
	 	 	
			this.creationform.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			e.preventDefault();
			var eventfieldset = e.target.closest("fieldset"),
		    valid = true;
	        for (i = 0; i < eventfieldset.elements.length; i++) {
	          if (!eventfieldset.elements[i].checkValidity()) {
	            eventfieldset.elements[i].reportValidity();
	            valid = false;
	            break;
	          }
	        }
	
	        if (valid) {
	          var self = this;
	         
	          makeCall("POST", "CreatePlaylist", e.target.closest("form"),
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message
	                if (req.status == 200) {
	                  orchestrator.refresh(message); 
	                } else {
	                  //self.alert.textContent = message;
	                  //self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
	     };
	 }
	 
	 function Genres(){
	 	 	
	 	this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetGenresData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var genres = JSON.parse(req.responseText);
	              	
					genres.forEach(function(genre) {
						option = document.createElement('option');
					    option.value = genre.genre;
					    option.textContent = genre.genre;
					    var x = document.getElementById("genre_select");
					    x.add(option);
					})
	              
	              if (next) next(); // show the default element of the list if present
	            }
	          } else {
	            ;
	          }
	        }
	      );
	    };
	 	
	 }
	 
	 function OrphanSongs(){
	 	
	 	this.show = function(next) {
	 	  document.getElementById("id_orphansongs").innerHTML = "";
	 	  var playlistid = currentPlaylist;
	      var self = this;
	      makeCall("GET", "GetOrphanSongsData?playlistId="+playlistid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var orphanSongs = JSON.parse(req.responseText);
	              	
					orphanSongs.forEach(function(orphanSong) {
						option = document.createElement('option');
					    option.value = orphanSong.songId;		    
					    option.textContent = orphanSong.title;
					    var x = document.getElementById("id_orphansongs");
					    x.add(option);
					})
	              
	              if (next) next();
	            }
	          } else {
	            ;
	          }
	        }
	      );
	    };
	 
	 }
	 
	 function AddSongToPlaylist(_addsongform){
	 
	 	this.addsongform = _addsongform;
	 	
	 	this.registerEvents = function(orchestrator) {	
	 		 	 	
			this.addsongform.querySelector("input[type='button'].submit").addEventListener("click", (e) => {
			e.preventDefault();
			var eventfieldset = e.target.closest("fieldset"),
		    valid = true;
	        for (i = 0; i < eventfieldset.elements.length; i++) {
	          if (!eventfieldset.elements[i].checkValidity()) {
	            eventfieldset.elements[i].reportValidity();
	            valid = false;
	            break;
	          }
	        }
	
	        if (valid) {
	          var self = this;
	          
	          var form = document.getElementById("id_addsongform");	          	          
	          var songid = form.songId.value;
	          	         
	          makeCall("POST", "AddSongToPlaylist?playlistId="+currentPlaylist+"&songId="+songid, null,
	            function(req) {
	              if (req.readyState == XMLHttpRequest.DONE) {
	                var message = req.responseText; // error message
	                if (req.status == 200) {
	                  songs.show(currentPlaylist);
	                  orphanSongs.show();
	                } else {
	                  //self.alert.textContent = message;
	                  //self.reset();
	                }
	              }
	            }
	          );
	        }
	      });
	     }; 
	 
	 }
	 
	 function SongDetails(options){
	 	this.songTitle = options['songTitle'];
	 	this.songImage = options['songImage'];
	 	this.songGenre = options['songGenre'];
	 	this.songArtist = options['songArtist'];
	 	this.albumTitle = options['albumTitle'];
	 	this.publYear = options['publYear'];
	 	this.audioPlayer = options['audioPlayer'];
	 	
	 	this.show = function(songid) {
	      var self = this;
	      makeCall("GET", "GetSongDetailsData?songId=" + songid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var song = JSON.parse(req.responseText);
	              self.update(song); // self is the object on which the function
	              // is applied
	            } else {
	              self.alert.textContent = message;

	            }
	          }
	        }
	      );
	    };
	    
	    this.update = function(s) {
	      this.songTitle.textContent = s.title;
	      
	      var imageUri = encodeURI(s.imageFile);
	      this.songImage.src = "http://localhost:8080/TIW_PlaylistManagement/GetImage?path="+imageUri;
	      
	      this.songGenre.textContent = "Genre: " + s.genre;
	      this.songArtist.textContent = "Artist: " + s.artist;
	      this.albumTitle.textContent = "Album Title: " + s.albumTitle;
	      this.publYear.textContent = "Publication Year: " + s.publicationYear;
	      
	      var audioUri = encodeURI(s.songFile);
	      this.audioPlayer.src = "http://localhost:8080/TIW_PlaylistManagement/GetSong?path="+audioUri;
	      
	    }
	 }
	 
	 function Sort(_sortbutton){
	 	
         this.sortbutton = _sortbutton;
		 //document.getElementById("id_savesort").style.visibility = "visible";
         //funzione che aggiunge l'evento di sorting al bottone (registerEvents)
         this.registerEvents = function() {
             var self = this;
             self.sortbutton.addEventListener("click", (e) => {
                 dragAndDrop.show();
             })
         }
	 }
	 
	 function DragAndDrop(_songsbody){
	 	this.songsbody = _songsbody;
	 	
	 	var i, row, titlecell, artistcell;
	 	
	  	this.show = function(){
	  	  var self = this;
	  	  
	      self.songsbody.innerHTML = ""; // empty the table body build updated list
	      
	      songslist.forEach(function(song) { // self visible here, not this
	      
	        row = document.createElement("tr");
	        //row.className = "playlistnotselected";
			row.value = song.songId;
	        
	        titlecell = document.createElement("td");
	        titlecell.textContent = song.title;
	        row.appendChild(titlecell);

	        artistcell = document.createElement("td");
	        artistcell.textContent = song.artist;
	        row.appendChild(artistcell);
	        	       

			row.setAttribute("draggable", "true");
			row.setAttribute("ondragstart", "dragStart(event)");
			row.setAttribute("ondragleave", "dragLeave(event)");
			row.setAttribute("ondragOver", "dragOver(event)");
			row.setAttribute("ondrop", "drop(event)");
			
	        self.songsbody.appendChild(row);
	      });
	    } 
 	 
	 }
	 
	 function SaveSort(_savesortbutton, _tablebody) {
		this.savesortbutton = _savesortbutton;
		this.tablebody = _tablebody;
		
		var self = this;
		
	 	this.registerEvents = function() {
			
			self.savesortbutton.addEventListener("click", (e) => {
				
				var rowsArray = Array.from(self.tablebody.querySelectorAll('tbody > tr'));
				var arrayOfSongs = new Array(rowsArray.length);
				
				for(var i=0; i<rowsArray.length; i++){
				 	arrayOfSongs[i] = rowsArray[i].value;
				}
	
				makeCall("GET", "SaveSort?playlistId="+currentPlaylist+"&songs="+arrayOfSongs, null,
			        function(req) {
			          if (req.readyState == 4) {
			            var message = req.responseText;
			            if (req.status == 200) {
			              
			              songs.show(currentPlaylist); // self is the object on which the function
			              // is applied
			            } else {
			              self.alert.textContent = message;
		
			            }
			          }
			        }
			      );
			});
		}
	 }
	  
	 function PageOrchestrator() {
	 
	   var alertContainer1 = document.getElementById("id_no_playlist_alert");
	   var alertContainer2 = document.getElementById("id_no_song_alert");
	   
	   
	   this.start = function() {
	   
	     groupId = 1;
	     personalMessage = new PersonalMessage(sessionStorage.getItem('username'), document.getElementById("id_username"));
	     personalMessage.show();
		
	
	     playlists = new Playlists(
	        alertContainer1,
	        document.getElementById("id_playliststable"),
	        document.getElementById("id_playlistcontainerbody"));
	
	     songs = new Songs({ // many parameters, wrap them in an object
	        alert: alertContainer2,
	        songscontainer: document.getElementById("id_songs_table"),
	        nextbutton: document.getElementById("id_nextbutton"),
	        sortbutton: document.getElementById("id_sortbutton"),
	        previousbutton: document.getElementById("id_previousbutton"),
	      });
	      //songs.registerEvents(this);
		  
		  
	      createSong = new CreateSong(document.getElementById("id_creationsongform"));
	      createSong.registerEvents(this);
		  
		  createPlaylist = new CreatePlaylist(document.getElementById("id_playlistcreationform"));
		  createPlaylist.registerEvents(this);
		  
		  addSongToPlaylist = new AddSongToPlaylist(document.getElementById("id_addsongform"));
		  addSongToPlaylist.registerEvents(this);
		  
		  genres = new Genres();
		  genres.show();
		  
		  orphanSongs = new OrphanSongs();
		  
		  
		  nextGroup = new NextGroup();
		  nextGroup.registerEvents();
		  
		  previousGroup = new PreviousGroup();
		  previousGroup.registerEvents();
		  
		  sort = new Sort(document.getElementById("id_sortbutton"));
		  sort.registerEvents();
		  
		  saveSort = new SaveSort(document.getElementById("id_savesort"), document.getElementById("id_dd_containerbody"));
		  //document.getElementById("id_savesort").style.visibility = "hidden";
		  saveSort.registerEvents();
			
		  dragAndDrop = new DragAndDrop(document.getElementById("id_dd_containerbody"));
		  
		  
		  songDetails = new SongDetails({
		  	songTitle: document.getElementById("id_songtitle"),
		  	songImage: document.getElementById("id_songimage"),
		  	songGenre: document.getElementById("id_genre"),
		  	songArtist: document.getElementById("id_artist"),
		  	albumTitle: document.getElementById("id_albumtitle"),
		  	publYear: document.getElementById("id_publicationyear"),
		  	audioPlayer: document.getElementById("id_audioplayer"),		  
		  });
		  
	      document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	        window.location.href = "LoginPage.html";
	      })
	    };
	
	
	    this.refresh = function() {
	      alertContainer1.textContent = "";
	      alertContainer2.textContent = "";
	      playlists.show();
	      //songs.reset();
	    };
	 }   
	})();