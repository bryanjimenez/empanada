var geocoder;
var map;
var lat=25.75906,lon=-80.37388;
var mypos;
var mymarker;
var interval=null;

var findme
var	legend

var infowindow=null;




function User (type) {
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
var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
var icons = {
	storm: {
		name: 'Storm',
		icon: iconBase + 'thunderstorm_maps.png',
		shadow: iconBase + 'thunderstorm_maps.shadow.png'
	},
	fire: {
		name: 'Fire',
		icon: iconBase + 'firedept_maps.png',
		shadow: iconBase + 'firedept_maps.shadow.png'
	},
	damage: {
		name: 'Damage',
		icon: iconBase + 'caution_maps.png',
		shadow: iconBase + 'caution_maps.shadow.png'
	},
	flood: {
		name: 'Flood',
		icon: iconBase + 'water_maps.png',
		shadow: iconBase + 'water_maps.shadow.png'
	},
		thunder: {
		name: 'Thunder',
		icon: iconBase + 'thunderstorm_maps.png',
		shadow: iconBase + 'thunderstorm_maps.shadow.png'
	},
	shelter: {
		name: 'Shelter',
		icon: iconBase + 'ranger_station_maps.png',
		shadow: iconBase + 'ranger_station_maps.shadow.png'
	},
	fuel: {
		name: 'Fuel',
		icon: iconBase + 'gas_stations_maps.png',
		shadow: iconBase + 'gas_stations_maps.shadow.png'
	},
	traffic: {
		name: 'Traffic',
		icon: iconBase + 'triangle_maps.png',
		shadow: iconBase + 'triangle_maps.shadow.png'
	},
	info: {
		name: 'Info',
		icon: iconBase + 'info_maps.png',
		shadow: iconBase + 'info_maps.shadow.png'
	}
};


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
  console.log('Longitude: ' + crd.longitude);
  console.log('More or less ' + crd.accuracy + ' meters.');
*/
	lat=pos.coords.latitude;
	lon=pos.coords.longitude;
	mypos = new google.maps.LatLng(lat, lon);
	//alert(mypos);
	
	dropPinMe();
};

function error(err) {
  console.warn('ERROR(' + err.code + '): ' + err.message);
	alert("Geolocation is not supported by this browser.");
};






function dropPinMe(){
	
	map.setCenter(mypos);

	if(mymarker)
		mymarker.setMap(null);
	
	mymarker = new google.maps.Marker({
		position: mypos,
		map: map,
		animation: google.maps.Animation.BOUNCE,
		icon: 'http://maps.google.com/mapfiles/arrow.png',
		title: 'You\'re Here'
	});
	
}

function initialize() {
	//window.setInterval(function() { alert(window.scrollTo(0,1));}, 2000); 
	
	//window.setInterval(function() { getLocation(); }, 2000); 

	findme = document.getElementById('findme');
	legend = document.getElementById('legend');
	
	
	geocoder = new google.maps.Geocoder();
	
	var mapOptions = {
		zoom: 7,
		center: new google.maps.LatLng(lat,lon),
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		disableDefaultUI: true
	};
	map = new google.maps.Map(document.getElementById('map-canvas'),
	mapOptions);


for (var key in icons) {
	var type = icons[key];
	var name = type.name;
	var icon = type.icon;
	var div = document.createElement('div');
	div.innerHTML = '<img alt="'+name+'" title="'+name+'" src="' + icon + '"> ';
	legend.appendChild(div);
}



	findme.onclick=	function() {
		/*
		// tracking mode OFF
		if(interval){
			findme.style.border='solid black 1px';
			window.clearInterval(interval);
			interval=null;
			disableMovement(false) 	
		}
		// tracking mode ON
		else{
			disableMovement(true)
			findme.style.border='solid red 1px';
			//interval=window.setInterval(function() { getLocation(); }, 3000); 	
			getLocation(); 	

		}
		*/

		navigator.geolocation.getCurrentPosition(success, error, options);
		
	};
	legend.onclick=function(){
		
		if(legend.style.height=='20px')
			legend.style.height='';		
		
		else
			legend.style.height='20px';	
	}


map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(legend);
map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(findme);

google.maps.event.addListener(map, 'click', function() {
	//close infobubble if we click on  map
	if (infowindow)infowindow.close();
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

function codeAddress() {
	var address = document.getElementById("zip").value;
	geocoder.geocode( { 'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			
			mypos=results[0].geometry.location;
			dropPinMe();	
		} else {
			alert("Geocode was not successful for the following reason: " + status);
		}
	});
}

function refresh(){
	
    var xmlHttp = null;

    xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", "refresh.php", false );
    xmlHttp.send( null );

    if(xmlHttp.status == 200) {
			//alert(xmlHttp.responseText);
			
			var tweets = JSON.parse(xmlHttp.responseText);
			//alert(tweets.f[0]);
			
			for (var i in tweets.t) {
				var filter = tweets.f[i];
				var tweet = tweets.t[i];
				var user=tweet.user.screen_name;
				var pic=tweet.user.profile_image_url;
				var text=tweet.text;
				var follow=tweet.user.followers_count;
				var time=tweet.created_at;
				
				//"coordinates": {"type": "Point", "coordinates": [-81.68738214, 27.96855823]}
				if(tweet.geo){
					var x=tweet.geo.coordinates[0];
					var y=tweet.geo.coordinates[1];
				}
				else{
					//TODO we need to come up with something better here
					var x=-81.68738214;
					var y=27.96855823;
				}
			
				var marker = new google.maps.Marker({
				position: new google.maps.LatLng(x,y),
				map: map,
				icon: icons[filter].icon,
				animation: google.maps.Animation.DROP,
				title: text
				});

				infobubble(marker, user, pic, text, follow, time);			  
			}		
    }
}

function infobubble(marker, user, pic, text, follow, time) {
	
	//"profile_image_url_https": "https://si0.twimg.com/profile_images/378800000397149614/2474965717ccf1a5d796a364486dd36a_normal.jpeg"
	  var d = new Date(time);
	  var date = d.getMonth()+1+"/"+d.getDay()+"/"+d.getFullYear();
	  
	  var contentString = ''+
		'<div id="content" style="width:304px; height:176px;">'+
			'<div id="siteNotice">'+
					'<div style="float:left;border:0px solid blue;">'+
						'<a href="https://twitter.com/'+user+'"><h4 id="firstHeading" class="firstHeading">'+user+'</h4></a>'+
					'</div>'+
					'<div style="float:right;border:0px solid red;width=50%;">'+
						'<p>+/-'+follow+'</p>'+
					'</div>'+
			'</div>'+
			'<div id="left" style="float:center;border:0px solid green;width:100%;">'+
			'</div>'+
			'<div id="bodyContent" style="float:left;border:0px solid blue;height:100px;">'+
				'<div style="float:left;border:0px solid blue;width:204px;">'+
					'<p style="max-width:200px">'+text+'</p>'+
				'</div>'+
				'<div style="float:right;border:0px solid red;width=100%;height=100%;">'+
					'<img name="userpic" src="'+pic+'"></img>'+
				'</div>'+
			'</div>'+
			'<div id="footer" style="float:right;">'+
				'<p>'+date+'</p>'+
			'</div>'+
		'</div>';
	
	// add click event
	google.maps.event.addListener(marker, 'click', function() {

	//close infobubble if we click on other bubble
	if (infowindow) infowindow.close();
    		
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

/*
for (var style in styles) {
	var name = style.name;
	var icon = style.icon;
	var div = document.createElement('div');
	div.innerHTML = '<img src="' + icon + 'width="50%"' +'> ' + name;
	legend.appendChild(div);
}
*/
