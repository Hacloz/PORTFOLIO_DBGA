import math  # Importa il modulo math per funzioni matematiche.
import random  # Importa il modulo random per generare numeri casuali.
import pygame  # Importa il modulo pygame per la creazione di giochi.
import tkinter as tk  # Importa il modulo tkinter per la creazione di GUI.
from tkinter import messagebox  # Importa la classe messagebox da tkinter per mostrare messaggi di avviso.

# Inizializza pygame
pygame.init()

# Definizione della classe Cubo, rappresenta i singoli blocchi del gioco Snake
class Cubo(object):
    righe = 20  # Numero di righe nella griglia del gioco.
    larghezza = 500  # Larghezza della finestra di gioco.

    def __init__(self, start, dirnx=1, dirny=0, color=(255,0,0)):
        self.pos = start  # Posizione del cubo nella griglia.
        self.dirnx = dirnx  # Direzione lungo l'asse x.
        self.dirny = dirny  # Direzione lungo l'asse y.
        self.color = color  # Colore del cubo.

    # Metodo per spostare il cubo
    def move(self, dirnx, dirny):
        self.dirnx = dirnx
        self.dirny = dirny
        self.pos = (self.pos[0] + self.dirnx, self.pos[1] + self.dirny)

    # Metodo per disegnare il cubo
    def draw(self, surface, eyes=False):
        dis = self.larghezza // self.righe  # Dimensione di ogni cella della griglia.
        i = self.pos[0]  # Coordinata x del cubo.
        j = self.pos[1]  # Coordinata y del cubo.

        # Disegna un rettangolo rappresentante il cubo sulla superficie di gioco.
        pygame.draw.rect(surface, self.color, (i*dis+1,j*dis+1, dis-2, dis-2))
        if eyes:  # Se eyes è True, disegna gli occhi del cubo.
            centro = dis // 2
            radius = 3
            circleMiddle = (i*dis+centro-radius,j*dis+8)
            circleMiddle2 = (i*dis + dis -radius*2, j*dis+8)
            pygame.draw.circle(surface, (0,0,0), circleMiddle, radius)
            pygame.draw.circle(surface, (0,0,0), circleMiddle2, radius)

# Definizione della classe Snake, rappresenta il serpente controllato dal giocatore
class Snake(object):
    body = []  # Lista che tiene traccia del corpo del serpente.
    turns = {}  # Dizionario per memorizzare i cambi di direzione.

    def __init__(self, color, pos):
        self.color = color  # Colore del serpente.
        self.head = Cubo(pos)  # Testa del serpente.
        self.body.append(self.head)  # Aggiungi la testa alla lista del corpo.
        self.dirnx = 0  # Direzione iniziale lungo l'asse x.
        self.dirny = 1  # Direzione iniziale lungo l'asse y.

    # Metodo per spostare il serpente
    def move(self):
        for event in pygame.event.get():  # Controlla gli eventi pygame.
            if event.type == pygame.QUIT:  # Se l'utente chiude la finestra, esce dal gioco.
                pygame.quit()

        keys = pygame.key.get_pressed()  # Ottiene i tasti premuti.

        for key in keys:
            if keys[pygame.K_LEFT]:  # Se viene premuto il tasto sinistro, cambia la direzione a sinistra.
                self.dirnx = -1
                self.dirny = 0
                self.turns[self.head.pos[:]] = [self.dirnx, self.dirny]

            elif keys[pygame.K_RIGHT]:  # Se viene premuto il tasto destro, cambia la direzione a destra.
                self.dirnx = 1
                self.dirny = 0
                self.turns[self.head.pos[:]] = [self.dirnx, self.dirny]

            elif keys[pygame.K_UP]:  # Se viene premuto il tasto su, cambia la direzione verso l'alto.
                self.dirnx = 0
                self.dirny = -1
                self.turns[self.head.pos[:]] = [self.dirnx, self.dirny]

            elif keys[pygame.K_DOWN]:  # Se viene premuto il tasto giù, cambia la direzione verso il basso.
                self.dirnx = 0
                self.dirny = 1
                self.turns[self.head.pos[:]] = [self.dirnx, self.dirny]

        for i, c in enumerate(self.body):  # Itera attraverso il corpo del serpente.
            p = c.pos[:]
            if p in self.turns:  # Se la posizione è registrata nei cambi di direzione.
                turn = self.turns[p]  # Ottieni il cambio di direzione.
                c.move(turn[0],turn[1])  # Sposta il cubo.
                if i == len(self.body)-1:  # Se il cubo è l'ultimo del corpo.
                    self.turns.pop(p)  # Rimuovi il cambio di direzione.
            else:
                if c.dirnx == -1 and c.pos[0] <= 0:  # Se il serpente va oltre i limiti della griglia a sinistra.
                    c.pos = (c.righe-1, c.pos[1])  # Riportalo al lato destro.
                elif c.dirnx == 1 and c.pos[0] >= c.righe-1:  # Se va oltre i limiti a destra.
                    c.pos = (0,c.pos[1])  # Riportalo al lato sinistro.
                elif c.dirny == 1 and c.pos[1] >= c.righe-1:  # Se va oltre i limiti in basso.
                    c.pos = (c.pos[0], 0)  # Riportalo in cima.
                elif c.dirny == -1 and c.pos[1] <= 0:  # Se va oltre i limiti in alto.
                    c.pos = (c.pos[0],c.righe-1)  # Riportalo in fondo.
                else:
                    c.move(c.dirnx,c.dirny)  # Sposta il cubo.

    # Metodo per ripristinare lo stato iniziale del serpente
    def reset(self, pos):
        self.head = Cubo(pos)  # Ricrea la testa del serpente.
        self.body = []  # Resetta la lista del corpo.
        self.body.append(self.head)  # Aggiungi la nuova testa alla lista.
        self.turns = {}  # Resetta i cambi di direzione.
        self.dirnx = 0  # Resetta la direzione iniziale.
        self.dirny = 1

    # Metodo per aggiungere un cubo al serpente
    def aggiungiCubo(self):
        tail = self.body[-1]  # Ottieni la coda del serpente
        dx, dy = tail.dirnx, tail.dirny  # Ottieni la direzione della coda.

        # Aggiungi un cubo alla posizione corretta in base alla direzione della coda.
        if dx == 1 and dy == 0:
            self.body.append(Cubo((tail.pos[0]-1,tail.pos[1])))
        elif dx == -1 and dy == 0:
            self.body.append(Cubo((tail.pos[0]+1,tail.pos[1])))
        elif dx == 0 and dy == 1:
            self.body.append(Cubo((tail.pos[0],tail.pos[1]-1)))
        elif dx == 0 and dy == -1:
            self.body.append(Cubo((tail.pos[0],tail.pos[1]+1)))

        self.body[-1].dirnx = dx  # Imposta la direzione del nuovo cubo.
        self.body[-1].dirny = dy

    # Metodo per disegnare il serpente
    def draw(self, surface):
        for i, c in enumerate(self.body):  # Itera attraverso il corpo del serpente.
            c.draw(surface, i == 0)  # Disegna il cubo, disegna gli occhi solo per la testa.

# Funzione per disegnare la griglia
def disegnaGriglia(larghezza, righe, surface):
    sizeBtwn = larghezza // righe  # Calcola la dimensione di ogni cella della griglia.

    x = 0
    y = 0
    for l in range(righe):  # Per ogni riga nella griglia.
        x = x + sizeBtwn  # Aggiorna la coordinata x.
        y = y + sizeBtwn  # Aggiorna la coordinata y.

        # Disegna le linee verticali e orizzontali della griglia.
        pygame.draw.line(surface, (255,255,255), (x,0),(x,larghezza))
        pygame.draw.line(surface, (255,255,255), (0,y),(larghezza,y))

# Funzione per ridisegnare la finestra del gioco
def ridisegnaFinestra(surface):
    surface.fill((0,0,0))  # Riempie la finestra di nero.
    s.draw(surface)  # Disegna il serpente sulla superficie.
    snack.draw(surface)  # Disegna lo snack sulla superficie.
    disegnaGriglia(width,righe, surface)  # Disegna la griglia sulla superficie.
    pygame.display.update()  # Aggiorna la finestra di gioco.

# Funzione per generare uno snack in posizione casuale
def randomSnack(righe, item):
    positions = item.body  # Ottieni le posizioni del corpo del serpente.

    while True:  # Ciclo finché non trova una posizione valida.
        x = random.randrange(righe)  # Genera una coordinata x casuale.
        y = random.randrange(righe)  # Genera una coordinata y casuale.
        if len(list(filter(lambda z:z.pos == (x,y), positions))) > 0:
            # Se la posizione generata è già occupata dal corpo del serpente, continua il ciclo.
            continue
        else:
            break  # Se la posizione è libera, esce dal ciclo e restituisce la posizione.

    return (x,y)  # Restituisce la posizione dello snack.

# Funzione per mostrare un messaggio
def message_box(subject, content):
    root = tk.Tk()  # Crea una nuova finestra tkinter.
    root.attributes("-topmost", True)  # Imposta la finestra come sempre in primo piano.
    root.withdraw()  # Nasconde la finestra principale.
    messagebox.showinfo(subject, content)  # Mostra il messaggio di avviso.
    try:
        root.destroy()  # Chiude la finestra tkinter.
    except:
        pass

# Funzione principale del gioco
def main():
    global width, righe, s, snack  # Variabili globali per larghezza, righe, serpente e snack.
    width = 500  # Larghezza della finestra di gioco.
    righe = 20  # Numero di righe nella griglia.
    win = pygame.display.set_mode((width, width))  # Crea la finestra di gioco.
    s = Snake((255,0,0), (10,10))  # Crea un nuovo serpente.
    snack = Cubo(randomSnack(righe, s), color=(0,255,0))  # Crea uno snack iniziale.
    flag = True  # Flag per il loop principale del gioco.

    clock = pygame.time.Clock()  # Oggetto Clock per gestire il tempo di gioco.

    while flag:  # Loop principale del gioco.
        pygame.time.delay(50)  # Aggiunge un ritardo di 50 millisecondi.
        clock.tick(10)  # Imposta il framerate del gioco a 10 frame al secondo.
        s.move()  # Muove il serpente.
        if s.body[0].pos == snack.pos:  # Se il serpente mangia lo snack.
            s.aggiungiCubo()  # Aggiunge un cubo al serpente.
            snack = Cubo(randomSnack(righe, s), color=(0,255,0))  # Crea uno nuovo snack.
        for x in range(len(s.body)):  # Controlla le collisioni con il corpo del serpente.
            if s.body[x].pos in list(map(lambda z:z.pos,s.body[x+1:])):
                print('Score:', len(s.body))  # Stampa il punteggio ottenuto.
                message_box('You Lost!', 'Play again...')  # Mostra un messaggio di sconfitta.
                s.reset((10,10))  # Riporta il serpente allo stato iniziale.
                break

        ridisegnaFinestra(win)  # Ridisegna la finestra di gioco.

    pygame.quit()  # Chiude il gioco quando esce dal loop principale.

main()  # Esegue la funzione principale del gioco.

       

