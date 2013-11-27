<?php
//header('Expires: Sun, 20 Jan 1985 00:00:00 GMT'); // date in the past
header('Cache-Control: no-cache, must-revalidate');
header('Content-type: text/html');
//header('Content-type: application/json');

//header('Pragma: no-cache');

// Report all errors except E_NOTICE
// This is the default value set in php.ini
//error_reporting(E_ALL ^ E_NOTICE);

ini_set('display_errors', '1');

//DANGER!!
ini_set('memory_limit','-1');




function main(){

	//$zipcode=getZipcode($_GET["lat"],$_GET["lng"]);

	//OPEN results file and insert into an array of lines
	echo exec('jar xf /usr/local/hadoop/elsa/empanada.jar empanada/vector2.json');

	$lines = file("empanada/vector2.json");

	$json = json_decode(implode ("",$lines),true);

//	foreach ($json as $key => $value){
//		echo  $key . ':' . $value;
//	}
	//echo "profanity: ".$json['profanity']['words']."\n";

	echo "<br/><h1>CATEGORIES</h1><br/><br/>".$json['categories'];

	echo "<br/><h1>KEYWORDS</h1><br/><br/>";

	foreach($json['keywords'] as $key => $value){
		echo $key.' : '.$value['category']."<br/>";
	}

	echo "<br/><h1>BIGRAMS</h1><br/><br/>";
        foreach($json['bigrams'] as $key => $value){
                echo $key.' : '.$value['category']."<br/>";
        }

	echo "<br/><h1>TRIGRAMS</h1><br/><br/>";
        foreach($json['trigrams'] as $key => $value){
                echo $key.' : '.$value['category']."<br/>";
        }
	
	//echo "\n";
//	echo json_encode($json);
}


//CALL MAIN
main();


//echo phpinfo();
//echo 'Last error: ', json_last_error();
?>
