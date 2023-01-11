// This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International License. 
// To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ 
// or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

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
	//Is Connected
	bool isConnected = false;

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
        }
    }



    // Use this for initialization 	
   public bool ConnectToServer(string name)
    {
        try
        {
            client = new TcpClient();
            client.Connect("localhost", port);
            stream = client.GetStream();
            StartReceiving();
            //InviaMessaggio("Hello, server!");
            // Subscribe to the quitting event
            Application.quitting += OnApplicationQuit;

			isConnected = true;
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
        /*
        if (Input.GetKeyDown(KeyCode.Space))
        {
            InviaMessaggio("Hello!");
        }
		*/
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
    }

    void OnApplicationQuit()
    {
        StopReceiving();
        client.Close();
    }
}
