//http://net.tutsplus.com/tutorials/javascript-ajax/how-to-test-your-javascript-code-with-qunit/
//http://qunitjs.com/

module('Module A');
test( "initialize() Completes", function() {
	//PASS
	notEqual(undefined,map, "initialize() defined a google.maps.Map object" );
	notEqual(undefined,legend, "initialize() defined a Legend object" );
	notEqual(undefined,compass, "initialize() defined a Compass object" );
	notEqual(undefined,places, "initialize() defined a Places object" );
	notEqual(undefined,geocoder, "initialize() defined a google.maps.Geocoder object" );
	notEqual(undefined,manager, "initialize() defined a MarkerManager object" );
	equal( initialize(),0, "initialize() terminated :)" );

	//FAIL	

});

module('Compass Module');
test( "Constructor", function() {
	var htmlobject = document.getElementById('findme');
    var compass = new Compass(htmlobject);
    
	notEqual(undefined,compass, "Compass constructor does not return undefined" );
	equal(htmlobject, compass.htmlObj,"Compass object returns original html object");
});
test( "html object can be clicked", 1, function() {
	var htmlobject = document.getElementById('findme');
    var compass = new Compass(htmlobject);
    
    htmlobject.onclick=function() {
		ok( true, "compass was clicked!" );
	};
    
    htmlobject.click();
});
test( "Compass is initially toggled OFF", 1, function() {
	var htmlobject = document.getElementById('findme');
    var compass = new Compass(htmlobject);
    
	equal(compass.status, true, "Compass is initially OFF" );
    
});
test( "Compass can be toggled ON", 1, function() {
	var htmlobject = document.getElementById('findme');
    var compass = new Compass(htmlobject);
    
    htmlobject.onclick=function() {
		equal(compass.status, true, "Compass was toggled ON" );
	};
    htmlobject.click();
    
});
test( "Compass can be toggled ON then back OFF", function() {
	var htmlobject = document.getElementById('findme');
    var compass = new Compass(htmlobject);
    
    htmlobject.onclick=function() {
		equal(compass.status, true, "Compass was toggled ON" );
	};
	
    htmlobject.click();
    htmlobject.onclick=null;   
        
    htmlobject.onclick=function() {
		equal(compass.status, false, "Compass was toggled back OFF" );
	};
    
    htmlobject.click();

});

module('Legend Module');
test( "Constuctor", function() {
    var htmlobject = document.getElementById('findme');
    var legend = new Legend(htmlobject);

	notEqual(undefined,legend, "Legend constructor does not return undefined" );
	equal(htmlobject, legend.htmlObj,"Legend object turns original html object");



});

module('Map Module');
test( "Map.rad2zoom", function() {
	

	
	// PASS
	equal(Map.rad2zoom(.5),15 ,"Test Radius(.5) to Zoom(15)");
	equal(Map.rad2zoom(1),14 ,"Test Radius(1) to Zoom(14)");
	equal(Map.rad2zoom(2),13 ,"Test Radius(2) to Zoom(13)");
	equal(Map.rad2zoom(4),12 ,"Test Radius(4) to Zoom(12)");
	equal(Map.rad2zoom(8),11 ,"Test Radius(8) to Zoom(11)");
	equal(Map.rad2zoom(16),10 ,"Test Radius(16) to Zoom(10)");
	equal(Map.rad2zoom(32),9 ,"Test Radius(32) to Zoom(9)");
});
test( "Map.zoom2rad", function() {
	
	// PASS
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

module('Places Module');
test( "initialize()", function() {

	
	expect(0);

});
