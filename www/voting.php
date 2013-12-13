<?php
error_reporting(E_ALL ^ E_NOTICE);


function main(){
	
	//echo './voting.sh '.$_GET['t'];
	//echo exec('./voting.sh '.$_GET['t']);
	//echo exec('./voting.sh 401898911071473664');
	
	//$t=$_GET['t'];
	//$t=401765119618609153;
	
	//echo exec("sed '/".$_GET['t']"/d' result.txt");
	echo exec("sed /$_GET['id']/d /home/jonathan/results/result.txt");
//	echo exec("touch /home/jonathan/results/result2.txt");
//	echo exec("whoami");

	
	
//	echo exec("> result.txt");
	//echo exec("> result.txt");
}

//CALL MAIN
main();


//echo phpinfo();
//echo 'Last error: ', json_last_error();
?>
