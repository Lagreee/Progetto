using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;
using UnityEngine.SceneManagement;

public class TCPClient : MonoBehaviour
{
    // The client socket
    TcpClient client;
    // The network stream
    NetworkStream stream;
    // A flag to control the receive loop
    bool isReceiving = false;
    // The receive thread
    Thread receiveThread;
    // The port we are using
    int port = 8080;
    // Is Connected
    bool isConnected = false;
    // Name
    String nome;

    //UpdateUITavoli
    bool UpdateUI = false;
    String[] UpdateUIStrings;

    //Connection to Tables
    bool connectedToTable = false;
    string tavolo = "";
    bool ChangeSceneTavoli = false;


    //Reference al Table manager per l'update dell'UI
    TableManager tm;

    //Reference al GameManager per l'update del campo di gioco
    GameManager gm;
    bool UpdateTable;
    string[] UpdateTableArgs = new string[9];

    // Singleton
    public static TCPClient Instance { get; private set; }

    private void Awake()
    {
        // If there is an instance, and it's not me, delete myself.

        if (Instance != null && Instance != this)
        {
            Destroy(this);
        }
        else
        {
            Instance = this;
            DontDestroyOnLoad(this.gameObject);
        }
    }



    // Use this for initialization 	
    public bool ConnectToServer(string name, string ip)
    {
        try
        {
            client = new TcpClient();
            client.Connect(ip, port);
            stream = client.GetStream();
            StartReceiving();
            Application.quitting += OnApplicationQuit;

            isConnected = true;
            this.nome = name;
            Debug.Log("Nome:" + nome);
        }
        catch (System.Exception)
        {
            isConnected = false;
        }
        return isConnected;
    }

    // Update is called once per frame
    void Update()
    {
        if (ChangeSceneTavoli)
        {
            SceneManager.LoadScene(1);
            ChangeSceneTavoli = false;
        }

        if (UpdateUI)
        {
            string[] tavolo1 = UpdateUIStrings[1].Split("-");
            Debug.Log(tavolo1[1] + " - " + tavolo1[2]);
            string[] tavolo2 = UpdateUIStrings[2].Split("-");
            Debug.Log(tavolo2[1] + " - " + tavolo2[2]);
            string[] tavolo3 = UpdateUIStrings[3].Split("-");
            Debug.Log(tavolo3[1] + " - " + tavolo3[2]);

            tm.SetUITavolo1(tavolo1[1], tavolo1[2]);
            tm.SetUITavolo2(tavolo2[1], tavolo2[2]);
            tm.SetUITavolo3(tavolo3[1], tavolo3[2]);

            UpdateUI = false;
        }

        if (connectedToTable)
        {
            SceneManager.LoadScene(2);
            connectedToTable = false;
        }

        if (UpdateTable)
        {
            if (UpdateTableArgs[0] == "add")
            {
                Debug.Log("Pos: " + UpdateTableArgs[2] + ", Carta: " + UpdateTableArgs[3]);
                gm.addCarta(UpdateTableArgs[2], UpdateTableArgs[3]);
            }
            else if (UpdateTableArgs[0] == "hiddenAdd")
            {
                gm.HiddenAdd();
            }
            else if (UpdateTableArgs[0] == "start")
            {
                gm.startGame();
            }
            else if (UpdateTableArgs[0] == "end")
            {
                gm.endGame();
            }
            else if (UpdateTableArgs[0] == "Nomi")
            {
                gm.UpdateName(UpdateTableArgs);
            }
            else if (UpdateTableArgs[0] == "bust")
            {
                gm.Bust(UpdateTableArgs[1]);
            }
            else if (UpdateTableArgs[0] == "blackjack")
            {
                gm.BlackJack(UpdateTableArgs[1]);
            }
            else if (UpdateTableArgs[0] == "results")
            {
                gm.Results(UpdateTableArgs);
            }
            else if (UpdateTableArgs[0] == "requestMove")
            {
                gm.requestMove();
            }
                
            UpdateTable = false;
        }


    }


    public void InviaMessaggio(string message)
    {
        // Convert the message to a byte array and send it
        byte[] data = Encoding.UTF8.GetBytes(message + "\n");
        stream.Write(data, 0, data.Length);
        Debug.Log("Messaggio inviato: " + message);
    }


    // Start receiving data from the server
    public void StartReceiving()
    {
        Debug.Log("Iniziato a ricevere");
        isReceiving = true;
        receiveThread = new Thread(new ThreadStart(ReceiveLoop));
        receiveThread.Start();
    }

    // Stop receiving data from the server
    public void StopReceiving()
    {
        isReceiving = false;
        receiveThread.Join();
    }

    // The receive loop
    void ReceiveLoop()
    {
        while (isReceiving)
        {
            // Check if data is available
            //if (stream.DataAvailable)
            if (stream.DataAvailable)
            {
                // Read data from the stream
                byte[] receivedData = new byte[256];
                int bytesReceived = stream.Read(receivedData, 0, receivedData.Length);
                string receivedMessage = Encoding.UTF8.GetString(receivedData, 0, bytesReceived);

                // Do something with the received data
                ProcessReceivedData(receivedMessage);
            }
        }
    }

    // Process the received data
    void ProcessReceivedData(string data)
    {
        Debug.Log("Messaggio Ricevuto: " + data);
        // Your code here
        // For example, you could parse the received data and update the game state accordingly
        String[] datiSeparati = data.Split(";");
        String comando = datiSeparati[0];

        switch (comando)
        {
            case "getName":
                InviaMessaggio(nome);
                break;

            case "ok":
                ChangeSceneTavoli = true;
                break;

            case "StatoTavolo":
                UpdateUIStrings = datiSeparati;
                UpdateUI = true;
                break;

            case "Connected":
                connectedToTable = true;
                break;

            case "add":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTableArgs[1] = datiSeparati[1];
                UpdateTableArgs[2] = datiSeparati[2];
                UpdateTableArgs[3] = datiSeparati[3];
                UpdateTable = true;
                break;

            case "hiddenAdd":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTable = true;
                break;

            case "start":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTable = true;
                break;

            case "end":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTable = true;
                break;

            case "Nomi":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTableArgs[1] = datiSeparati[1];
                UpdateTableArgs[2] = datiSeparati[2];
                UpdateTableArgs[3] = datiSeparati[3];
                UpdateTableArgs[4] = datiSeparati[4];
                UpdateTableArgs[5] = datiSeparati[5];
                UpdateTableArgs[6] = datiSeparati[6];
                UpdateTableArgs[7] = datiSeparati[7];
                UpdateTable = true;
                break;
            
            case "results":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTableArgs[1] = datiSeparati[1];
                UpdateTableArgs[2] = datiSeparati[2];
                UpdateTableArgs[3] = datiSeparati[3];
                UpdateTableArgs[4] = datiSeparati[4];
                UpdateTableArgs[5] = datiSeparati[5];
                UpdateTableArgs[6] = datiSeparati[6];
                UpdateTableArgs[7] = datiSeparati[7];
                UpdateTable = true;
                break;

            case "bust":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTableArgs[1] = datiSeparati[2];
                UpdateTable = true;
                break;
            
            case "blackjack":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTableArgs[1] = datiSeparati[2];
                UpdateTable = true;
                break;

            case "requestMove":
                UpdateTableArgs[0] = datiSeparati[0];
                UpdateTable = true;
                break;

            default:
                Debug.Log("messaggio non riconosciuto:" + data);
                break;
        }
    }

    void OnApplicationQuit()
    {
        Quit();
    }

    public string getName()
    {
        return nome;
    }

    public void Quit()
    {
        InviaMessaggio("quit");
        StopReceiving();
        client.Close();
    }

    public void setTm(TableManager t)
    {
        this.tm = t;
    }

    public void setGameManager(GameManager g)
    {
        this.gm = g;
    }

    public void connectToTable(string Tavolo)
    {
        tavolo = Tavolo;
        InviaMessaggio("connectToTable[" + Tavolo + "]");
    }


}
