<?php
error_reporting(E_ALL ^ E_NOTICE);


function main(){
	
	//echo './voting.sh '.$_GET['t'];
	//echo exec('./voting.sh '.$_GET['t']);
	//echo exec('./voting.sh 401898911071473664');
	
	//$t=$_GET['t'];
	//$t=401765119618609153;
	
	echo exec("sed -i '/".$_GET['t']"/d' ./result.txt");
	//echo exec("sed -i '/$t/d' result.txt");
	//echo exec("> result.txt");
}

//CALL MAIN
main();


//echo phpinfo();
//echo 'Last error: ', json_last_error();
?>
