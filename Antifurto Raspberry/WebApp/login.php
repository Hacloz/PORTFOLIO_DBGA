<?php
    function getClientIP() {
        $ipaddress = '';
        if (isset($_SERVER['HTTP_CLIENT_IP']))
            $ipaddress = $_SERVER['HTTP_CLIENT_IP'];
        else if(isset($_SERVER['HTTP_X_FORWARDED_FOR']))
            $ipaddress = $_SERVER['HTTP_X_FORWARDED_FOR'];
        else if(isset($_SERVER['HTTP_X_FORWARDED']))
            $ipaddress = $_SERVER['HTTP_X_FORWARDED'];
        else if(isset($_SERVER['HTTP_FORWARDED_FOR']))
            $ipaddress = $_SERVER['HTTP_FORWARDED_FOR'];
        else if(isset($_SERVER['HTTP_FORWARDED']))
            $ipaddress = $_SERVER['HTTP_FORWARDED'];
        else if(isset($_SERVER['REMOTE_ADDR']))
            $ipaddress = $_SERVER['REMOTE_ADDR'];
        else
            $ipaddress = 'UNKNOWN';
        return $ipaddress;
    }

    session_start();

    $array=array(
        "hamza.bensaid@casa.it"=>"ciao123",
    );

    if(isset($_POST['email']) && isset($_POST['password'])){
        $mail=$_POST['email'];
        $psw=$_POST['password'];
        $ip = getClientIP();
        if(array_key_exists($mail, $array) && $array[$mail]===$psw) {
            $_SESSION['email']=$mail;
            $_SESSION['ip']=$ip;
            $_SESSION['logout']=1;
            header("Location: webapp.php");
            exit();
        }else{
            echo "Utente sconosciuto<br>";
            echo "<a href='index.html'>Torna indietro</a>";
        }
    }
    

    
?>  