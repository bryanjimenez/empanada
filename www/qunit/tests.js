//http://net.tutsplus.com/tutorials/javascript-ajax/how-to-test-your-javascript-code-with-qunit/
//http://qunitjs.com/


module('Compass Module');
	test( "Constructor", function() {
		var htmlobject = document.getElementById('compass');
		var compass = new Compass(htmlobject);
		
		notEqual(undefined,compass, "Compass constructor does not return undefined" );
		equal(htmlobject, compass.htmlObj,"Compass object returns original html object");
	});

	test( "Compass Toggling", function() {
		var htmlobject = document.getElementById('compass');
		var compass = new Compass(htmlobject);
		
		equal(compass.status(), false, "Compass is initially OFF" );


		var old=htmlobject.onclick;
		
		
		htmlobject.onclick=function() {
			old();
			equal(compass.status(), true, "Compass was toggled ON" );

			old();
			equal(compass.status(), false, "... then OFF" );
		};
		


		htmlobject.click();

	});
	


module('Legend Module');
	test( "Constuctor", function() {
		var htmlobject = document.getElementById('legend');
		var legend = new Legend(htmlobject);

		notEqual(undefined,legend, "Legend constructor does not return undefined" );
		equal(htmlobject, legend.htmlObj,"Legend object turns original html object");

	});
	test( "Legend is initially not collapsed", function() {
		var htmlobject = document.getElementById('legend');
		var legendtitle = document.getElementById('legendtitle');
		var legend = new Legend(htmlobject);

		equal(legend.htmlObj.style.height, '', "Legend is initially NOT collapsed" );
	});
	test( "Legend collapses on click", function() {
		var htmlobject = document.getElementById('legend');
		var legend = new Legend(htmlobject);
		var legendtitle = legend.htmlObj.children[0];

		var old = legendtitle.onclick;

		legendtitle.onclick=function(){
			old();
			equal(legend.htmlObj.style.height, '20px', "Legend collapses on click" );
		}

		legendtitle.click();

	});

module('Map Module');
	test( "Map.rad2zoom", function() {
		

		
		equal(Map.rad2zoom(.5),15 ,"Test Radius(.5) to Zoom(15)");
		equal(Map.rad2zoom(1),14 ,"Test Radius(1) to Zoom(14)");
		equal(Map.rad2zoom(2),13 ,"Test Radius(2) to Zoom(13)");
		equal(Map.rad2zoom(4),12 ,"Test Radius(4) to Zoom(12)");
		equal(Map.rad2zoom(8),11 ,"Test Radius(8) to Zoom(11)");
		equal(Map.rad2zoom(16),10 ,"Test Radius(16) to Zoom(10)");
		equal(Map.rad2zoom(32),9 ,"Test Radius(32) to Zoom(9)");
	});
	test( "Map.zoom2rad", function() {
		
		equal(Map.zoom2rad(15), .5, "Test Zoom(15) to Radius(.5)");
		equal(Map.zoom2rad(14), 1, "Test Zoom(14) to Radius(1)");
		equal(Map.zoom2rad(13), 2, "Test Zoom(13) to Radius(2)");
		equal(Map.zoom2rad(12), 4, "Test Zoom(12) to Radius(4)");
		equal(Map.zoom2rad(11), 8, "Test Zoom(11) to Radius(8)");
		equal(Map.zoom2rad(10), 16, "Test Zoom(10) to Radius(16)");
		equal(Map.zoom2rad(9), 32, "Test Zoom(9) to Radius(32)");
	});
	test( "Test equality between zoom and radius", function() {
		// TEST Google Map's 20 zoom levels
		for(var i=0;i<=20;i++){
			equal(Map.zoom2rad(i),Map.zoom2rad(Map.rad2zoom(Map.zoom2rad(i))) ,"Test Zoom("+i+") == Zoom(Radius(Zoom("+i+")))");
		}

	});
/*
module('Places Module');
	test( "places", function() {

		equal(1,1,"1");

	});
*/	
module('Module A');
	test( "initialize() Completes", function() {
		notEqual(undefined,map, "initialize() defined a google.maps.Map object" );
		notEqual(undefined,legend, "initialize() defined a Legend object" );
		notEqual(undefined,compass, "initialize() defined a Compass object" );
		notEqual(undefined,places, "initialize() defined a Places object" );
		notEqual(undefined,geocoder, "initialize() defined a google.maps.Geocoder object" );
		notEqual(undefined,manager, "initialize() defined a MarkerManager object" );
		equal( initialize(),0, "initialize() terminated :)" );

	});
	
	
	
	
module('System Test1');
	test( "map refresh", function() {
		refresh="../refresh.php";
		jsonF="../json/filters.json";

		//lat=25.6;
		//lng=-80.6;
		
		lat=25.7588981;
		lng=-80.37406669;
			
		zoom = map.getZoom();
		rad = Map.zoom2rad(zoom);
		
		notEqual( map.getCenter().lat(),lat, "map is not initially at this location" );
				
		equal(update(),null,"update worked");

		equal( map.getCenter().lat(),lat, "map is moved to new coordinates" );



		alert(Markers.markers.length);
		//equal( manager.getMarkerCount(0),1, "got markers" );
		//manager.getMarker(x,y,0)==null
		

	});
	
	
