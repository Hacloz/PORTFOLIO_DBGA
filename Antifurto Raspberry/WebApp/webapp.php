<?php session_start(); 
    if(!isset($_SESSION['logout'])){
        header("Location: errore.php");
    }
?>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Controllo Dispositivi IoT</title>
    <link rel="stylesheet" href="styleapp.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="main-container">
        <div class="menu" id="menu">
            <h2>Menu</h2>
            <a href="#soggiorno" class="menu-button">Soggiorno</a>
            <a href="#camera1" class="menu-button">Camera da Letto 1</a>
            <a href="logout.php" class="logout-button">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </div>
        <div class="container">
            <button class="menu-toggle" onclick="toggleMenu()">☰ Menu</button>
            <h1>Controllo Dispositivi IoT</h1>
            <div class="device-control" id="soggiorno">
                <h2>Soggiorno</h2>
                <div class="device">
                    <h3>Lampada</h3>
                    <form action="process_device.php" method="post">
                        <input type="hidden" name="device" value="Lampada">
                        <button type="submit" name="accendi1" value="on" class="btn-on">Accendi</button>
                        <button type="submit" name="spegni1" value="off" class="btn-off">Spegni</button>
                    </form>
                </div>
                <div class="device">
                    <h3>TV</h3>
                    <form action="process_device.php" method="post">
                        <input type="hidden" name="device" value="TV">
                        <button type="submit" name="action" value="on" class="btn-on">Accendi</button>
                        <button type="submit" name="action" value="off" class="btn-off">Spegni</button>
                    </form>
                </div>
            </div>
            <div class="device-control" id="camera1">
                <h2>Camera da Letto 1</h2>
                <div class="device">
                    <h3>Luce Principale</h3>
                    <form action="process_device.php" method="post">
                        <input type="hidden" name="device" value="Luce Principale">
                        <button type="submit" name="action" value="on" class="btn-on">Accendi</button>
                        <button type="submit" name="action" value="off" class="btn-off">Spegni</button>
                    </form>
                </div>
                <div class="device">
                    <h3>Ventilatore</h3>
                    <form action="process_device.php" method="post">
                        <input type="hidden" name="device" value="Ventilatore">
                        <button type="submit" name="action" value="on" class="btn-on">Accendi</button>
                        <button type="submit" name="action" value="off" class="btn-off">Spegni</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <script>
        function toggleMenu() {
            document.getElementById('menu').classList.toggle('open');
        }
    </script>
</body>
</html>
