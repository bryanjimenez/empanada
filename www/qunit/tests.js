//http://net.tutsplus.com/tutorials/javascript-ajax/how-to-test-your-javascript-code-with-qunit/
//http://qunitjs.com/

test( "initialize()", function() {
	module('Module A');
	//PASS
	equal( initialize(),0, "initialize() terminated :)" );
	//FAIL
	equal( initialize(),2, "initialize() terminated :)" );
	
	
	
	//location.search="?lat=1";
	//equal( location.search,"?lat=1", "Params" );

});
