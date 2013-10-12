var geocoder;
var map;
var lat = 25.75906, lng = -80.37388, zoom=14; rad=1;
var mypos;
var mymarker;
var markers=[];
var markerst=[];

var watching=null;

var findme;
var legend;
var filter=[];
var forbidden="image/forbidden.png";
var infowindow = null;

//https://developers.google.com/maps/documentation/javascript/reference#Map

/*
 * example pseudoclass
 */
function User(type) {
    this.type = type;
    this.color = "red";
    this.getInfo = function() {
        return this.color + ' ' + this.type + ' apple';
    };
}




/* some icons are listed here
 * 
 * http://kml4earth.appspot.com/icons.html
 */
var icons;
var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';


/*
 * Great place to test google api code
 * http://code.google.com/apis/ajax/playground/
 */

/*
 * http://www.w3schools.com/html/html5_geolocation.asp
 */

var options = {
    enableHighAccuracy: true,
    timeout: 5000,
    maximumAge: 0
};

function success(pos) {
    /*  var crd = pos.coords;
     
     console.log('Your current position is:');
     console.log('Latitude : ' + crd.latitude);
     console.log('lnggitude: ' + crd.lnggitude);
     console.log('More or less ' + crd.accuracy + ' meters.');
     */
    lat = pos.coords.latitude;
    lng = pos.coords.longitude;
    mypos = new google.maps.LatLng(lat, lng);
    //alert(mypos);

    pinMe(true);
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
  
	findme.style.border='solid black 1px';
	findme.style.backgroundImage = 'url(\'image/arrow_off.png\')';
	disableMovement(false);
	navigator.geolocation.clearWatch(watching);
	watching=null;
	pinMe(false);
	
	
    console.warn('ERROR(' + err.code + '): ' + err.message);
//    alert("Geolocation is not supported by this browser.");
    alert(msg);
}

function pinMe(t) {

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
			animation: google.maps.Animation.BOUNCE,
			icon: 'http://maps.google.com/mapfiles/arrow.png',
			title: 'You\'re Here'
		});
	}
}


function initialize() {

    findme = document.getElementById('findme');
    legend = document.getElementById('legend');
    legendtitle = document.getElementById('legendtitle');


	getfilters();

    geocoder = new google.maps.Geocoder();

    var mapOptions = {
        zoom: zoom,
        center: new google.maps.LatLng(lat, lng),
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        //mapTypeId: google.maps.MapTypeId.SATELLITE,
        disableDefaultUI: true
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);






    findme.onclick = function() {

        // tracking mode OFF
		if(watching){
			findme.style.border='solid black 1px';
			findme.style.backgroundImage = 'url(\'image/arrow_off.png\')';
			disableMovement(false);
			navigator.geolocation.clearWatch(watching);
			watching=null;
			pinMe(false);
			}
		// tracking mode ON
		else{
			disableMovement(true);
			findme.style.border='solid red 1px';
			findme.style.backgroundImage = 'url(\'image/arrow_on.png\')';
			watching=navigator.geolocation.watchPosition(success, error, options);
			refresh(); 	
		}

        //just once 
		//navigator.geolocation.getCurrentPosition(success, error, options);
    };
    
    legendtitle.onclick = function() {

        if (legend.style.height == '20px')
            legend.style.height = '';

        else
            legend.style.height = '20px';
    }


    map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);
    map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(findme);

    google.maps.event.addListener(map, 'click', function() {
        //close infobubble if we click on  map
        if (infowindow)
            infowindow.close();
    });
	google.maps.event.addListener(map, 'dragend', function(){
		refresh();
		fpl();
	});

    /*
     getLocation();
     map.setCenter(mypos);
     */

}




/* geocoding
 * 
 * https://developers.google.com/maps/documentation/javascript/geocoding?csw=1
 */

// REVERSE GEOCODING
//https://developers.google.com/maps/documentation/geocoding/#ReverseGeocoding
//maps.google.com/maps/api/geocode/json?latlng=25.705501,-80.359855&components=postal_code&sensor=false

function codeAddress() {
    var address = document.getElementById("zip").value;
    geocoder.geocode({'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {

            mypos = results[0].geometry.location;
            //pinMe();
            map.setCenter(mypos);
            refresh();

        } else {
            alert("Geocode was not successful for the following reason: " + status);
        }
    });
}

function getfilters() {

    var xmlHttp = null;
    xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange=function()
	{
		if (xmlHttp.readyState==4 && xmlHttp.status==200)
		{
			//alert(xmlHttp.responseText);
			icons = JSON.parse(xmlHttp.responseText);

			for (var key in icons) {
				filter.push(key.toLowerCase());
				var type = icons[key];
				var name = type.name;
				var icon = type.icon;
				var div = document.createElement('div');
				div.innerHTML = '<div title="' + key + '" style="width:32px;height:37px;background-image: url(' + icon + ')" onclick="filters(this);"></div> ';
				//div.innerHTML = '<div><img alt="' + name + '" title="' + name + '" src="" onclick="filters(this);"></div> ';
				legend.appendChild(div);
			}
        }		
	}
    xmlHttp.open("GET", "filters.json", true);
    xmlHttp.send(null);
}

/* Thanks to
 * http://www.fplmaps.com/
 */
function fpl() {

    var xmlHttp = null;

    xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange=function()
	{
		if (xmlHttp.readyState==4 && xmlHttp.status==200  && xmlHttp.responseText)
		{

        //alert(xmlHttp.responseText);

        var fpl = JSON.parse(xmlHttp.responseText);
        //alert(tweets.f[0]);

        for (var i in fpl.o) {
            var filter = "powerout";
            var outage = fpl.o[i];

            if (!outage.status)
                continue;

            var cause = outage.Cause;
            var pic = "http://www.fpl.com/fplcommon/wrapper/1024images/w_topLogo.gif";
            var status = outage.status;
            var reported = "Reported " + outage.dateReported;
            var updated = "Updated " + outage.lastUpdated;


            var x = outage.lat;
            var y = outage.lng;
			var contentString = '' +
			'<div id="content" style="width:304px; height:176px;">' +
			'<div id="siteNotice">' +
			'<div style="float:left;border:0px solid blue;">' +
                    '<h4 id="firstHeading" class="firstHeading">' + cause + '</h4>' +
			'</div>' +
			'<div style="float:right;border:0px solid red;width=50%;">' +
			'</div>' +
			'</div>' +
			'<div id="left" style="float:center;border:0px solid green;width:100%;">' +
			'</div>' +
			'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
			'<div style="float:left;border:0px solid blue;width:204px;">' +
			'<p style="max-width:200px">' + status + '</p>' +
			'</div>' +
			'<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
			'<img name="userpic" src="' + pic + '"></img>' +
			'</div>' +
			'</div>' +
			'<div id="footer" style="float:right;">' +
            '<p>' + reported + '<br/>' + updated + '</p>' +
			'</div>' +
			'</div>';

            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(x, y),
                map: map,
                icon: icons[filter].icon,
                animation: google.maps.Animation.DROP,
                title: status
            });


			markers.push(marker);
			markerst.push(filter);
			
					
            infobubble(marker, contentString);
            

        }		}
	}
	
	
	var s = "fpl.php?lat="+lat+"&lng="+lng+"&rad="+rad+"&olat="+olat+"&olng="+olng+"&orad="+orad+"&filter="+filter;
	//alert(s);
	
    xmlHttp.open("GET", s, true);
    
    //xmlHttp.open("GET", "StormFeedRestoreZoom2.json", true);
    xmlHttp.send(null);
    

}

function refresh() {
	
	//TODO
	// currently the refresh will pull the same markers if the center isnt far from the old center
	// could avoid this by using the old center and only getting back the new markers
	
	//alert("!");
    var xmlHttp = null;

    xmlHttp = new XMLHttpRequest();

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
						'<a href="https://twitter.com/' + user + '"><h4 id="firstHeading" class="firstHeading">' + user + '</h4></a>' +
						'</div>' +
						'<div style="float:right;border:0px solid red;width=50%;">' +
						'<p>+/-' + follow + '</p>' +
						'</div>' +
						'</div>' +
						'<div id="left" style="float:center;border:0px solid green;width:100%;">' +
						'</div>' +
						'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">' +
						'<div style="float:left;border:0px solid blue;width:204px;">' +
						'<p style="max-width:200px">' + text + '</p>' +
						'</div>' +
						'<div style="float:right;border:0px solid red;width=100%;height=100%;">' +
						'<img name="userpic" src="' + pic + '"></img>' +
						'</div>' +
						'</div>' +
						'<div id="footer" style="float:right;">' +
						'<p>' + date + '</p>' +
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
					icon: icons[filter].icon,
					animation: google.maps.Animation.DROP,
					title: text
				});
					
					markers.push(marker);
					markerst.push(filter);
					
					
				infobubble(marker, contentString);
			}
		}
	}
	//PARAMETERS
	olat=lat;
	olng=lng;
	orad=zoom2rad(zoom);
	lat=map.getCenter().lat();
	lng=map.getCenter().lng();
	zoom=map.getZoom();
	
	//alert(map.getZoom());
	//alert(zoom2rad(map.getZoom()));
	rad=zoom2rad(zoom);
	var s = "refresh.php?lat="+lat+"&lng="+lng+"&rad="+rad+"&olat="+olat+"&olng="+olng+"&orad="+orad+"&filter="+filter;
	//alert(s);
	
    xmlHttp.open("GET", s, true);
    xmlHttp.send(null);

   	//alert(markers[filter][0].toString());

}

function toggleMarkers(category, t){ 
	if (infowindow)
		infowindow.close();
	
	for (var i = 0; i < markers.length; i++)      
		if(markerst[i]==category)
			markers[i].setVisible(t);        
}


//15 = 1/2, 14 = 1, 13 = 2, 12 = 4, 11 = 8, 10 = 16
//-1 = 1/2, 0 = 1, 1 = 2, 2 = 4, 3 = 8, 4 = 16
//2^(14-zoom) = radius
//ln(x^y) = y*ln(x)
//ln(2^(14-zoom)) = ln(radius) = (14-zoom) * ln(2)
//ln(radius)/ln(2) -14 = -zoom
//zoom = 14 - ln(radius)/ln(2)
//throw  round on there to make sure we get an integer
//and don't make the g-maps API angry
function rad2zoom(radius){
    return Math.round(14-Math.log(radius)/Math.LN2);
}

function zoom2rad(zoom){
    return Math.round(Math.pow(Math.E,(14-zoom)/Math.LN2));
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

google.maps.event.addDomListener(window, 'load', initialize);


function disableMovement(disable) {
    var mapOptions;
 
    
    if (disable) {
        mapOptions = {
            draggable: false,
            //scrollwheel: false,
            disableDoubleClickZoom: true,
            //zoomControl: false
        };
    } else {
        mapOptions = {
            draggable: true,
            //scrollwheel: true,
            disableDoubleClickZoom: false,
            //zoomControl: true
        };
    }
    map.setOptions(mapOptions);
}


//ADD REMOVE Filters from filter array
//ENABLE DISABLE legend icon
function filters(obj){
	
var div = document.createElement('div');
	
	var name = obj.title.toLowerCase();

	if(obj.innerHTML==''){
		obj.innerHTML = '<img src="'+forbidden+'">';		
		filter.splice(filter.indexOf(name),1);
		toggleMarkers(name,false);
	}
	else{
		obj.innerHTML = '';
		filter.push(name);
		toggleMarkers(name,true);
	
	}
}
/*
 for (var style in styles) {
 var name = style.name;
 var icon = style.icon;
 var div = document.createElement('div');
 div.innerHTML = '<img src="' + icon + 'width="50%"' +'> ' + name;
 legend.appendChild(div);
 }
 */
