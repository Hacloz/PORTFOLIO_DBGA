<?php
session_start();

// Verifica che la sessione sia valida
if(!isset($_SESSION['logout'])){
    header("Location: errore.php");
    exit;
}

// Verifica che il form sia stato inviato
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if(isset($_POST['accendi1'])){
        $con = new mysqli("localhost","root","","sicurezza");
        if (mysqli_connect_errno()) {
            echo("Connessione non effettuata: ".mysqli_connect_error()."<BR>");                         
            exit();
        }
        $conta=0;
        //Creazione query di comando sql
        $email=$_SESSION['email'];
        $sql = "INSERT INTO iot(ID_ultimostato, stato_allarme, utente_richiesta, `log`, ip) VALUES (null, true, '".$email."', '".date('Y-m-d h:i:s', time())."', '".$_SESSION['ip']."')";
        //Esecuzione query che restituisce $ris
        $ris = $con->query($sql) or die ("Query fallita!");
        //Rilascio connessione
        $con->close();
        header("Location: webapp.php");
    }
    if(isset($_POST['spegni1'])){
        $con = new mysqli("localhost","root","","sicurezza");
        if (mysqli_connect_errno()) {
            echo("Connessione non effettuata: ".mysqli_connect_error()."<BR>");                         
            exit();
        }
        $conta=0;
        //Creazione query di comando sql
        $email=$_SESSION['email'];
        $sql = "INSERT INTO iot(`stato_allarme`, `utente_richiesta`, `log`, ip) VALUES (false, '".$email."', '".date('Y-m-d h:i:s', time())."', '".$_SESSION['ip']."')";
        //Esecuzione query che restituisce $ris
        $ris = $con->query($sql) or die ("Query fallita!");
        //Rilascio connessione
        $con->close();
        header("Location: webapp.php");
    }
    // Puoi eseguire qui la logica per accendere o spegnere il dispositivo
    // Ad esempio, puoi registrare l'azione in un file di log o in un database
}
?>