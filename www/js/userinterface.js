//GOOGLE Globals
var geocoder;
var map;
var manager;

//EMPANADA Globals
var lat = 25.75906, lng = -80.37388, zoom = 14, rad = 1;


// refresh.php will be used if you point the browser to :8080
// refresh.php is a backup/experimental
var refresh="refresh.php";
//var refresh="cache";
//var vote =	"voting.php";
//var vote =	"cache/downvote";


var jsonF="json/filters.json";

var mypos;
var LOG=false;
var live=false;

function color(n){
	var c;
	switch(true)
	{
		case (n==666):
			c = "";
			break;
		case (n>6):
			c = "red/";
			break;
		case (n>4 && n<7):
			c = "orange/";
			break;
		case (n<5):
			c = "green/";
			break;
		default:
			c = "";
	}
	return c;
}


var Markers = {
    markers: [],
    markerst: [],
    window: null,
    closeAll: function(){
		if (Markers.window)
            Markers.window.close();
	},
    toggle: function(category, t) {
        if (this.window)
            this.window.close();

        for (var i = 0; i < this.markers.length; i++)
            if (this.markerst[i] == category)
                this.markers[i].setVisible(t);
    },
    push: function(m, f, c) {
        this.markers.push(m);
        this.markerst.push(f);


        google.maps.event.addListener(m, 'click', function() {

            //close infobubble if we click on other bubble
            if (Markers.window)
                Markers.window.close();

            Markers.window = new google.maps.InfoWindow({
                content: c,
                arrowStyle: 2,
                arrowSize: 10,
                ShadowStyle: 1

            });
            Markers.window.open(map, m);
        });
    }
}

//SINGLETON
var legend;
function Legend(parent) {
    var _this = this;
    var forbidden = "image/forbidden.png";

    var filters = [];
    var prefilters;
    var icons;
    var collapsed=false;
    
    this.isCollapsed = function(){return collapsed};
    this.setCollapsed = function(v){
		//alert(_this.htmlObj.style.height);
		if(v)
			_this.htmlObj.style.height = '20px';
		else
			_this.htmlObj.style.height = '';
		
		collapsed=v;
	};
    
    this.htmlObj = parent;

    this.add = function(item) {
        filters.push(item);
    };

    this.remove = function(item) {
        filters.splice(filters.indexOf(item), 1);
    };

    this.getFilters = function() {
        return filters;
    };
    this.getIcons = function(key) {
		if(key=='raw')
			return 'letter_t.png'
		else	
			return icons[key].icon;
    };
    this.setPreFilters = function(f) {
        prefilters = f.split(",");
        //alert(prefilters);
    }
    this.toggle = function(obj) {
        var name = obj.id.toLowerCase().split('_')[0];
        
        // Dissable
        if (obj.innerHTML == '') {
            obj.innerHTML = '<img src="' + forbidden + '">';
            this.remove(name);
            //toggleMarkers(name,false);
            Markers.toggle(name, false);
        }
        // Enable
        else {
            obj.innerHTML = '';
            this.add(name);
            Markers.toggle(name, true);
            update();
        }
    };


    
    
    var lTitle = document.createElement('div');
    lTitle.innerHTML="Legend"
	parent.appendChild(lTitle);

	lTitle.onclick = function() {
		//alert(_this.isCollapsed());
		if (!_this.isCollapsed())
			_this.setCollapsed(true);
		else
			_this.setCollapsed(false);
	} 
    
     
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function(par) {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
        {
            //alert(xmlHttp.responseText);
            icons = JSON.parse(xmlHttp.responseText);
            for (var key in icons) {
                key = key.toLowerCase();
                //(key);

                filters.push(key);
                //alert(this.filters[0]);
                var type = icons[key];
                var name = type.name;
                var icon = "image/red/" + type.icon;
                var div = document.createElement('div');

                if (prefilters && prefilters.indexOf(key) < 0) {
                    div.innerHTML = '<div id="' + key + '_filter" title="' + name + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="legend.toggle(this);"><img src="' + forbidden + '"></div> ';
                    _this.remove(key);
                } else {
                    div.innerHTML = '<div id="' + key + '_filter" title="' + name + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="legend.toggle(this);"></div> ';
                }
                parent.appendChild(div);
            }
        }
    }
    xmlHttp.open("GET", jsonF, true);
    xmlHttp.send(null);
}



var places;
function Places() {

    //https://developers.google.com/places/training/additional-places-features
    this.searchNear = function(obj) {
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
                '<span style="font-weight:400">' + place.name + '</span>' +
                '</div>' +
                '<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
                '<span>' + place.formatted_address + '<br/><br/>' +
                '' + (place.website == null ? '' : '<a href="' + place.website + '" target="_blank">' + place.website + '</a>') + '<br/>' +
                '' + (place.formatted_phone_number == null ? '' : place.formatted_phone_number) + '<br/>' +
                '' + (place.rating == null ? '' : 'Rating: ' + place.rating) + '</span><br/><br/>' +
                '<a href="' + (place.url == null ? '' : place.url) + '" target="_blank">More Info</a>' +
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
            Markers.window.setContent(contentString);
            Markers.window.open(map, this);
        });
    }
}


//SINGLETON
var compass;
function Compass(obj) {
    var _this = this;
    var mymarker;
    var watching = null;
    
    this.status = function(){
		return watching!=null;
	};
	
    
    var needle_off = 'image/arrow_off.png';
    var needle_on = 'image/arrow_on.png'
    this.htmlObj = obj;

    this.htmlObj.onclick = function() {
        _this.toggle();
    };

    /*
     * Great place to test google api code
     * http://code.google.com/apis/ajax/playground/
     */
    this.dropPin = function(t) {
        //toggle off
        if (!t && mymarker)
            mymarker.setMap(null);
        else {
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

        switch (error.code)
        {
            case error.PERMISSION_DENIED:
                msg = "User denied the request for Geolocation."
                break;
            case error.POSITION_UNAVAILABLE:
                msg = "Location information is unavailable."
                break;
            case error.TIMEOUT:
                msg = "The request to get user location timed out."
                break;
            case error.UNKNOWN_ERROR:
                msg = "An unknown error occurred."
                break;
        }
        _this.off();

        //console.warn('ERROR(' + err.code + '): ' + err.message);
        alert("Geolocation is not supported by your browser at this time.");
        //alert(msg);
    }
    ;

    this.off = function() {
        _this.htmlObj.style.backgroundImage = 'url(\'' + needle_off + '\')';
        //disableMovement(false);
        navigator.geolocation.clearWatch(watching);
        watching = null;
        _this.dropPin(false);
    };

    this.toggle = function() {
        // tracking mode OFF
        if (watching!=null) {
            _this.htmlObj.style.backgroundImage = 'url(\'' + needle_off + '\')';
            navigator.geolocation.clearWatch(watching);
            watching = null;
            _this.dropPin(false);
        }
        // tracking mode ON
        else {
            _this.htmlObj.style.backgroundImage = 'url(\'' + needle_on + '\')';
            watching = navigator.geolocation.watchPosition(success, error, options);
        }
    };
}


//STATIC Class(Object) MAP
var Map = {
    //15 = 1/2, 14 = 1, 13 = 2, 12 = 4, 11 = 8, 10 = 16
    //-1 = 1/2, 0 = 1, 1 = 2, 2 = 4, 3 = 8, 4 = 16
    //2^(14-zoom) = radius
    //ln(x^y) = y*ln(x)
    //ln(2^(14-zoom)) = ln(radius) = (14-zoom) * ln(2)
    //ln(radius)/ln(2) -14 = -zoom
    //zoom = 14 - ln(radius)/ln(2)
    //throw  round on there to make sure we get an integer
    //and don't make the g-maps API angry

    rad2zoom: function(radius) {
        return Math.round(14 - Math.log(radius) / Math.LN2);
    },
    zoom2rad: function(zoom) {
        return (Math.pow(2, (14 - zoom)));
    },
    /* geocoding
	 * 
	 * https://developers.google.com/maps/documentation/javascript/geocoding?csw=1
	 */

	// REVERSE GEOCODING
	//https://developers.google.com/maps/documentation/geocoding/#ReverseGeocoding
	//maps.google.com/maps/api/geocode/json?latlng=25.705501,-80.359855&components=postal_code&sensor=false
    codeAddress: function(address){
		geocoder.geocode({'address': address}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {

				mypos = results[0].geometry.location;
				map.setCenter(mypos);
				update();

			} else {
				alert("Geocode was not successful for the following reason: " + status);
			}
		});
	}
}



function update() {
   
	var xmlHttp = new XMLHttpRequest();

	xmlHttp.onreadystatechange = function()
	{
		if (xmlHttp.readyState == 4 && xmlHttp.status == 200 && xmlHttp.responseText)
		{
			//if(!xmlHttp.responseText)
			//	return;

			var tweets = JSON.parse(xmlHttp.responseText);
			
			if(tweets.t!=null)
				LOG&&console.log("Results: "+tweets.t.length);

			//alert(tweets.t.length);

			for (var i in tweets.t) {
				
				//alert(i);
				var filter = tweets.f[i];
				var tweet = tweets.t[i];
				var rank = tweets.r[i];

				var user = tweet.user.screen_name;
				var pic = tweet.user.profile_image_url;
				var text = tweet.text;
				var follow = tweet.user.followers_count;
				var friends = tweet.user.friends_count;
				var c = new Date(tweet.created_at);

				
				//alert(tweet.created_at);

				//need to cleanup by Andres
				if(tweet.created_at.indexOf("lastUpdated :")<0){
					var d=c.toDateString().split(" ");
					var t=c.toLocaleTimeString().split(":");
					var ampm = c.toLocaleTimeString().split(" ")[1];
					var created = d[0]+", "+d[1]+" "+d[2]+" "+t[0]+":"+t[1]+" "+ampm;
				}
				else{
					var created="";
					text = text.split("**")[0]+text.split("**")[1]+text.split("**")[3];

				}
				//"coordinates": {"type": "Point", "coordinates": [-81.68738214, 27.96855823]}
				if (tweet.geo) {
					var x = tweet.geo.coordinates[0];
					var y = tweet.geo.coordinates[1];
				}
				else {
					//TODO we need to come up with something better here
					var x = 25.758102;
					var y = -80.373633;
					//geocode stuff here
					//25.758102, -80.373633 middle of fiu's pond
				}

				//var s =	"cache/downvote?filter="+filter+"&lat="+x+"&lng="+y+"&id="+tweet.id_str;


				var contentString = '' +
						'<div id="content" style="width:304px; height:176px;">' +
						'<div id="siteNotice">' +
						'<div style="float:left;border:0px solid blue;">' +
						'<a href="https://twitter.com/' + user + '" target="_blank" title="Visit '+user+'\'s profile"><h4 id="firstHeading" class="firstHeading">' + user + '</h4></a>' +
						'</div>' +
						'<div style="float:right;border:0px solid red;width=50%;">' +
						'<p>Followers: ' + follow + '<br/>' +
						'Friends: ' + friends + '</p>' +
						'</div>' +
						'</div>' +
						'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
						'<div style="float:left;border:0px solid blue;width:204px;">' +
						'<p style="max-width:200px">' + tagAnchors(text) + '</p>' +
						'</div>' +
						'<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
						'<img name="userpic" src="' + pic + '"></img>' +
						'</div>' +
						'</div>' +
						'<div id="options" style="float:left;border:0px solid blue;width:100%;">' +
						'<div id="footer" style="float:right;border:0px solid black;">' +
						'<p>' + created + '</p>' +
						'</div>' +
						//http://stackoverflow.com/questions/12823579/
						//Open iOS 6 native map from URL
						'<a href="http://maps.google.com/maps/?saddr=Current%20Location&daddr=' + user + '@' + x + ',' + y + '" title="Navigate here from current position" target="_blank">Go here</a> | ' +
						'<a href="javascript:showSearch(true);" title="Search for places near here">Search Nearby</a> | ' +
						'<a href="javascript:vote(\''+filter+'\','+x+','+y+',\''+tweet.id_str+'\');" title="Vote up/down this tweet">Vote</a>' +
						'<div id="detail" style="visibility:hidden;float:left;border:0px solid blue;width:100%;">' +
						'<form  role="search" onsubmit="places.searchNear(this.children[0]);return false;">' +
						'<input id="target" type="text"><img src="image/x.png" style="margin-left:5px;" onclick="showSearch(false);">' +
					   '</div>' +
						'</div>' +
						'</div>';

			
				
				
				var image = {
					url: pic,
					size: new google.maps.Size(71, 71),
					origin: new google.maps.Point(0, 0),
					anchor: new google.maps.Point(17, 34),
					scaledSize: new google.maps.Size(25, 25)
				};


				var marker = new google.maps.Marker({
					position: new google.maps.LatLng(x, y),
					//map: map,
					icon: refresh!="raw.php"?"image/"+color(rank) + legend.getIcons(filter):image,
					animation: google.maps.Animation.DROP,
					title: text
				});
				
				//LOG&&console.log("tweet: "+xmlHttp.responseText);


				if(manager.getMarker(x,y,0)==null){
					Markers.push(marker, filter, contentString);
					manager.addMarker(marker,0);
					manager.refresh();
				}else{
					//TODO
					// do something to avoid tweets with same location appearing right ontop of eachother
				}
			}
		}
	}
	//PARAMETERS
	lat = map.getCenter().lat();
	lng = map.getCenter().lng();
	zoom = map.getZoom();
	//TODO
	// rad cannot be < 1 if not there wont be resultsd
	rad = Map.zoom2rad(zoom);



	//var s = "refresh.php?lat=" + lat + "&lng=" + lng + "&rad=" + rad + "&olat=" + olat + "&olng=" + olng + "&orad=" + orad + "&filter=" + legend.getFilters();
	var s = refresh+"?lat=" + lat + "&lng=" + lng + "&rad=" + rad + "&filter=" + legend.getFilters();
	LOG&&console.log("Request: "+s);

	//need to take these out because if NEW tweets are in the result they will be ommited
	//olat = lat;
	//olng = lng;
	//orad = Map.zoom2rad(zoom);

	xmlHttp.open("GET", s, true);
	xmlHttp.send(null);
}


// good regex info
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp?redirectlocale=en-US&redirectslug=JavaScript%2FReference%2FGlobal_Objects%2FRegExp
// https://support.google.com/a/answer/1371417?hl=en
// http://www.cheatography.com/davechild/cheat-sheets/regular-expressions/

function tagAnchors(s) {
var p = /https?:[^( |,|”|“|")]*/g;

	if (p.test(s)){
		s = s.replace(p,"<a href=$& target='_blank'>$&</a>");
	}
	
	return s
}
/*
function vote(x,y,filter,id){
	//alert(manager.getMarker(x,y,0)==null);
	
	var m = manager.getMarker(x,y,0);
	m.setMap(null);
	manager.removeMarker(m);
	manager.refresh();

}
* */

function vote(filter,x,y,id) {
	var s =	"cache/downvote?filter="+filter+"&lat="+x+"&lng="+y+"&id="+id;

	var xmlHttp = new XMLHttpRequest();

	xmlHttp.onreadystatechange = function()
	{
		if (xmlHttp.readyState == 4 && xmlHttp.status == 200 && xmlHttp.responseText)
		{
			
			LOG&&console.log("Cache Response: "+xmlHttp.responseText);
			var cacheResponse =xmlHttp.responseText.split(': ')[1];

			var x = s.split('lat=')[1].split('&')[0];
			var y = s.split('lng=')[1].split('&')[0];
			
			if(cacheResponse==0){
				var m = manager.getMarker(x,y,0);
				m.setMap(null);
				manager.removeMarker(m);
				manager.refresh();
			}	
		}
	}
	LOG&&console.log("Request: "+s);


	xmlHttp.open("GET", s, true);
	xmlHttp.send(null);
}


//used by info bubbles for search nearby
function showSearch(t) {
    document.getElementById('detail').style.visibility = (t==true)?'':'hidden';
}




//https://developers.google.com/maps/documentation/javascript/reference#Map




function initialize() {

	// OBJECT INITIALIZATIONS
	
    compass = new Compass(document.getElementById('compass'));
    places = new Places();
    geocoder = new google.maps.Geocoder();
    legend = new Legend(document.getElementById('legend'));

	// URL PARAMETERS

    if (location.search.indexOf('lat=') > -1)
        lat = location.search.split('lat=')[1].split('&')[0];
    if (location.search.indexOf('lng=') > -1)
        lng = location.search.split('lng=')[1].split('&')[0];
    if (location.search.indexOf('filter=') > -1)
        legend.setPreFilters(location.search.split('filter=')[1].split('&')[0]);
	if (location.search.indexOf('zoom=') > -1)
        zoom = parseInt(location.search.split('zoom=')[1].split('&')[0]);					//this needs to be a number
	if (location.search.indexOf('live=') > -1)
        live = location.search.split('live=')[1].split('&')[0]=='true';						//this needs to be a boolean
	if (location.search.indexOf('log=') > -1)
        LOG = location.search.split('log=')[1].split('&')[0]=='true';					//this needs to be a boolean

	// BACKUP PORT
	if (location.port == '8080'){
		refresh="cache";
		//vote="cache/downvote";
	}	
    if (location.search.indexOf('raw=') > -1)
        refresh = location.search.split('raw=')[1].split('&')[0]=='true'?'raw.php':refresh;	//this needs to be a boolean

    var mapOptions = {
        zoom: zoom,
        center: new google.maps.LatLng(lat, lng),
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        //mapTypeId: google.maps.MapTypeId.SATELLITE,
        disableDefaultUI: true
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

    var mgrOptions = {borderPadding: 0, maxZoom: 1};
    manager = new MarkerManager(map, mgrOptions);


    // EVENT HANDLERS
    
	// Do an update every time AFTER the map is moved
    google.maps.event.addListener(map, "tilesloaded", function() { 
		update();
		garbagecollect();
	});
 
	
	// Clear all windows BEFORE the map is changed
    google.maps.event.addListener(map, "zoom_changed", function() { 
		Markers.closeAll();
	});
    google.maps.event.addListener(map, "dragend", function() { 
		Markers.closeAll();
		//compass.off();
	});    
	google.maps.event.addListener(map, 'click', function() {
        Markers.closeAll();
    });
	
	
	
	// If map is 'live' update every ...
    live&&setInterval(function(){update();garbagecollect();},5000);


	// MISC
    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend.htmlObj);
    map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(compass.htmlObj);
    
    
    return 0;
}

function garbagecollect(){
	LOG&&console.log("GC Before:"+manager.getMarkerCount(0));

	while(manager.getMarkerCount(0)>1000){
		manager.removeMarker(Markers.markers.shift());
	}		
	LOG&&console.log("GC After:"+manager.getMarkerCount(0));

}


// UI EVENTS
google.maps.event.addDomListener(window, 'load', initialize);
