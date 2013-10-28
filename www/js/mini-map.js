//GOOGLE Globals
var geocoder;
var map;
var manager;

//EMPANADA Globals
var fiu;
var lat = 25.75906, lng = -80.37388, zoom=14; rad=1;
var olat = 0, olng = 0, ozoom=14; orad=0;
var mypos;

var markers=[];
var markerst=[];

var infowindow = null;



var legend;
function Legend(parent) {
	var forbidden="image/forbidden.png";

	var filters=[];
	var icons;
	this.div = parent;

	this.add = function (item) {
		filters.push(item);
	};

	this.remove = function (item) {
		filters.splice(filters.indexOf(item),1);
	};
			
    this.getFilters = function() {
        return filters;
    };
    this.getIcons = function(key) {
        return icons[key].icon;
    };
    
    this.toggle = function (obj) {
		var name = obj.id.toLowerCase().split('_')[0];
		if(obj.innerHTML==''){
			obj.innerHTML = '<img src="'+forbidden+'">';		
			this.remove(name)
			toggleMarkers(name,false);
		}
		else{
			obj.innerHTML = '';
			this.add(name);
			toggleMarkers(name,true);
		}
	};
    
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange=function(par) {
		if (xmlHttp.readyState==4 && xmlHttp.status==200)
		{
			//alert(xmlHttp.responseText);
			icons = JSON.parse(xmlHttp.responseText);
			for (var key in icons) {
				//alert(key.toLowerCase()+" "+location.search.split('&')[2].split('=')[1]);
				
				if(key.toLowerCase()==location.search.split('&')[2].split('=')[1])
				filters.push(key.toLowerCase());
				//alert(this.filters[0]);
				var type = icons[key];
				var name = type.name;
				var icon = "image/red/"+type.icon;
				var div = document.createElement('div');
				//div.innerHTML = '<div id="'+key+'_filter" title="' + name + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="legend.toggle(this);"></div> ';
				
				//parent.appendChild(div);
			}
			//Do a refresh once filters are up
			//alert(legend.getFilters());
						
			refresh();
		}		
	}
	xmlHttp.open("GET", "json/filters.json", true);
	xmlHttp.send(null);
}


var places;
function Places() {
				
	//https://developers.google.com/places/training/additional-places-features
	this.searchNear = function (obj){
		var request = {
			location: new google.maps.LatLng(lat, lng),
			radius: 500,
			//types: ['restaurant']
			keyword: obj.value 
		};
		//infowindow = new google.maps.InfoWindow();
		var service = new google.maps.places.PlacesService(map);
		service.nearbySearch(request, callback);
	};
	
	function callback(results, status) {
		if (status == google.maps.places.PlacesServiceStatus.OK) {
			for (var i = 0; i < results.length; i++) {
				var request = {
					reference: results[i].reference
				};
				service = new google.maps.places.PlacesService(map);
				service.getDetails(request, callback_detail);
			}
		}
	}
	function callback_detail(place, status) {
		if (status == google.maps.places.PlacesServiceStatus.OK)
			createMarker(place);
	}

	//https://developers.google.com/places/documentation/search#PlaceSearchRequests 
	//https://developers.google.com/maps/documentation/javascript/examples/place-search-pagination  
	function createMarker(place) {
		var contentString = '' +
		'<div id="content" style="width:304px;height:176px;">' +
			'<div id="siteNotice">' +
				'<span style="font-weight:400">'+place.name+'</span>' +
			'</div>' +
			'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
				'<span>'+place.formatted_address+'<br/><br/>'+
				
				''+(place.website==null?'':'<a href="'+place.website+'" target="_blank">'+place.website+'</a>')+'<br/>' +
				''+(place.formatted_phone_number==null?'':place.formatted_phone_number)+'<br/>' +
				''+(place.rating==null?'':'Rating: '+place.rating)+'</span><br/><br/>' +
				
				'<a href="'+(place.url==null?'':place.url)+'" target="_blank">More Info</a>' +
			'</div>' +
		'</div>';
		
		var image = {
			url: place.icon,
			size: new google.maps.Size(71, 71),
			origin: new google.maps.Point(0, 0),
			anchor: new google.maps.Point(17, 34),
			scaledSize: new google.maps.Size(25, 25)
		};

		var marker = new google.maps.Marker({
			map: map,
			icon: image,
			position: place.geometry.location
		});

		google.maps.event.addListener(marker, 'click', function() {
			infowindow.setContent(contentString);
			infowindow.open(map, this);
		});
	}
}

var compass;
function Compass(obj) {
	var _this = this;
	var mymarker;
	var watching=null;
	var needle_off = 'image/arrow_off.png';
	var needle_on = 'image/arrow_on.png'
	
	this.htmlObj = obj;	

    document.getElementById('legendtitle').onclick = function() {

        if (_this.htmlObj.style.height == '20px')
            _this.htmlObj.style.height = '';
        else
            _this.htmlObj.style.height = '20px';
    }

	obj.onclick = function (){
		_this.toggle();
	};
	

    
	/*
	* Great place to test google api code
	* http://code.google.com/apis/ajax/playground/
	*/
    this.dropPin = function (t) {
		//toggle off
		if(!t && mymarker)
				mymarker.setMap(null);	
		else{
			map.setCenter(mypos);

			if (mymarker)
				mymarker.setMap(null);

			mymarker = new google.maps.Marker({
				position: mypos,
				map: map,
				//animation: google.maps.Animation.BOUNCE,
				icon: 'image/red.png',
				title: 'You\'re Here'
			});
		}
	}
	
	/*
	 * http://www.w3schools.com/html/html5_geolocation.asp
	 */
	var options = {
		enableHighAccuracy: true,
		timeout: 5000,
		maximumAge: 0
	};

	function success(pos) {
		lat = pos.coords.latitude;
		lng = pos.coords.longitude;
		mypos = new google.maps.LatLng(lat, lng);
		_this.dropPin(true);
	}

	function error(err) {
		var msg;

		switch(error.code)
		{
			case error.PERMISSION_DENIED:
				msg="User denied the request for Geolocation."
				break;
			case error.POSITION_UNAVAILABLE:
				msg="Location information is unavailable."
				break;
			case error.TIMEOUT:
				msg="The request to get user location timed out."
				break;
			case error.UNKNOWN_ERROR:
				msg="An unknown error occurred."
				break;
		}	  
		_this.off();

		//console.warn('ERROR(' + err.code + '): ' + err.message);
		alert("Geolocation is not supported by your browser at this time.");
		//alert(msg);
	};
	
    this.off = function(){
			_this.htmlObj.style.backgroundImage = 'url(\''+needle_off+'\')';
			//disableMovement(false);
			navigator.geolocation.clearWatch(watching);
			watching=null;
			_this.dropPin(false);
	};
    
    this.toggle = function() {
		// tracking mode OFF
		if(watching){
			_this.htmlObj.style.backgroundImage = 'url(\''+needle_off+'\')';
			//disableMovement(false);
			navigator.geolocation.clearWatch(watching);
			watching=null;
			_this.dropPin(false);
		}
		// tracking mode ON
		else{
			//disableMovement(true);
			_this.htmlObj.style.backgroundImage = 'url(\''+needle_on+'\')';
			watching=navigator.geolocation.watchPosition(success, error, options);
			//refresh(); 	
		}
	};   
}


function initialize() {

	//zipshow = document.getElementById('zipshow');
	
	//alert(lat=location.search.split('&')[0].split('=')[1]);

	lat=location.search.split('&')[0].split('=')[1];
	lng=location.search.split('&')[1].split('=')[1];

	fiu = new google.maps.LatLng(lat, lng);

	legend = new Legend();
	//compass = new Compass(document.getElementById('findme'));
	//places = new Places();
	
    //geocoder = new google.maps.Geocoder();

    var mapOptions = {
        draggable: false,
        scrollwheel: false,
        disableDoubleClickZoom: true,
		zoomControl: false,
        zoom: zoom,
        center: fiu,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        //mapTypeId: google.maps.MapTypeId.SATELLITE,
        disableDefaultUI: true
    };
    
    map = new google.maps.Map(document.getElementById('mini-map-canvas'),
            mapOptions);
            
    //var mgrOptions = { borderPadding: 50, maxZoom: 15};
	//manager = new MarkerManager(map, mgrOptions);        

	//alert(location.search);

	// EVENT HANDLERS


/*
    google.maps.event.addListener(map, 'click', function() {
        //close infobubble if we click on  map
        if (infowindow)
            infowindow.close();
    });
	google.maps.event.addListener(map, 'dragend', function(){
		refresh();
		fpl();
		
		compass.off();
	});
*/
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend.div);
    //map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(compass.htmlObj);
}


function refresh() {
	
    var xmlHttp = new XMLHttpRequest();

	xmlHttp.onreadystatechange=function()
	{
		if (xmlHttp.readyState==4 && xmlHttp.status==200 && xmlHttp.responseText)
		{
			//if(!xmlHttp.responseText)
			//	return;
			
			var tweets = JSON.parse(xmlHttp.responseText);
			
				
			//alert(tweets.t.length);

			for (var i in tweets.t) {
				//alert(i);
				var filter = tweets.f[i];
				var tweet = tweets.t[i];

				var user = tweet.user.screen_name;
				var pic = tweet.user.profile_image_url;
				var text = tweet.text;
				var follow = tweet.user.followers_count;
				var time = tweet.created_at;
				var d = new Date(time);
				var date = d.getMonth() + 1 + "/" + d.getDay() + "/" + d.getFullYear();

				var contentString = '' +
						'<div id="content" style="width:304px; height:176px;">' +
							'<div id="siteNotice">' +
								'<div style="float:left;border:0px solid blue;">' +
									'<a href="https://twitter.com/' + user + '" target="_blank"><h4 id="firstHeading" class="firstHeading">' + user + '</h4></a>' +
								'</div>' +
								'<div style="float:right;border:0px solid red;width=50%;">' +
									'<p>+/-' + follow + '</p>' +
								'</div>' +
							'</div>' +
							'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
								'<div style="float:left;border:0px solid blue;width:204px;">' +
									'<p style="max-width:200px">' + text + '</p>' +
								'</div>' +
								'<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
									'<img name="userpic" src="' + pic + '"></img>' +
								'</div>' +
							'</div>' +

							'<div id="options" style="float:left;border:0px solid blue;width:100%;">'+
								'<div id="footer" style="float:right;border:0px solid black;">' +
									'<p>' + date + '</p>'+
								'</div>' +
								'<a href="javascript:showSearch();">Search Nearby</a>' +
									'<div id="detail" style="visibility:hidden;float:left;border:0px solid blue;width:100%;">'+
										'<input id="target" type="text"><button id="search" onclick="places.searchNear(this.previousSibling)" >Search</button>'+
									'</div>' +	
							'</div>' +
						'</div>';

				//"coordinates": {"type": "Point", "coordinates": [-81.68738214, 27.96855823]}
				if (tweet.geo) {
					var x = tweet.geo.coordinates[0];
					var y = tweet.geo.coordinates[1];
				}
				else {
					//TODO we need to come up with something better here
					var x = -81.68738214;
					var y = 27.96855823;
				}
				
				
				var marker = new google.maps.Marker({
					position: new google.maps.LatLng(x, y),
					map: map,
					icon: "image/red/"+legend.getIcons(filter),
					animation: google.maps.Animation.DROP,
					title: text
				});

				markers.push(marker);
				markerst.push(filter);
				//manager.addMarker(marker,12);
				//manager.refresh();
					
				infobubble(marker, contentString);
			}
		}
	}
	//PARAMETERS
	lat=map.getCenter().lat();
	lng=map.getCenter().lng();
	zoom=map.getZoom();
	rad=5;
	
	
	var s = "refresh.php?lat="+lat+"&lng="+lng+"&rad="+rad+"&olat="+olat+"&olng="+olng+"&orad="+orad+"&filter="+legend.getFilters();
	//alert(s);

	
	
    xmlHttp.open("GET", s, true);
    xmlHttp.send(null);
}



function infobubble(marker, contentString) {

    // add click event
    google.maps.event.addListener(marker, 'click', function() {

        //close infobubble if we click on other bubble
        if (infowindow)
            infowindow.close();

        infowindow = new google.maps.InfoWindow({
            content: contentString,
            arrowStyle: 2,
            arrowSize: 10,
            ShadowStyle: 1

        });
        infowindow.open(map, marker);
    });
}




//EVENTS

google.maps.event.addDomListener(window, 'load', initialize);
