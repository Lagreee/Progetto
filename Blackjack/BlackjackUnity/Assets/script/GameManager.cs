using System;
using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class GameManager : MonoBehaviour
{
    [SerializeField] private Transform pos0, pos1, pos2, pos3, pos4, pos5, pos6, posDealer;
    [SerializeField] TextMeshProUGUI nameText1, nameText2, nameText3, nameText4, nameText5, nameText6, nameText7;
    [SerializeField] TextMeshProUGUI statoText1, statoText2, statoText3, statoText4, statoText5, statoText6, statoText7, statoTextDealer;
    
    [SerializeField] private GameObject quitBT, addBT, stayBT;
    int[] numCarte = new int[8];
    int layer = 1;

    List<GameObject> listaCarte = new List<GameObject>();

    public void startGame()
    {
        Debug.Log("STARTGAME");
        quitBT.SetActive(false);
        numCarte = new int[8];
        
        for (int i = 0; i < numCarte.Length; i++)
        {
            numCarte[i] = 0;
        }
        layer = 1;

        statoText1.text = "";
        statoText2.text = "";
        statoText3.text = "";
        statoText4.text = "";
        statoText5.text = "";
        statoText6.text = "";
        statoText7.text = "";
        statoTextDealer.text = "";

        addBT.GetComponent<Button>().interactable = false;
        stayBT.GetComponent<Button>().interactable = false;
    }

    public void endGame()
    {
        quitBT.SetActive(true);

        GameObject[] carteDaEliminare = GameObject.FindGameObjectsWithTag("Carta");
        Debug.Log(carteDaEliminare.ToString());
        foreach (GameObject c in carteDaEliminare)
        {
            Destroy(c);
        }

        statoTextDealer.text = "";
        statoText1.text = "";
        statoText2.text = "";
        statoText3.text = "";
        statoText4.text = "";
        statoText5.text = "";
        statoText6.text = "";
        statoText7.text = "";
    }

    // Start is called before the first frame update
    void Start()
    {
        TCPClient.Instance.InviaMessaggio("getNomiGiocatoriNelTavolo");
        TCPClient.Instance.setGameManager(this);
        for (int i = 0; i < numCarte.Length; i++)
        {
            numCarte[i] = 0;
        }
        layer = 1;
        addBT.GetComponent<Button>().interactable = false;
        stayBT.GetComponent<Button>().interactable = false;
    }

    public void addCarta(string Pos, string nomeCarta)
    {
        GameObject carta = new GameObject();
        carta.tag = "Carta";
        listaCarte.Add(carta);
        //Aggiungi Sprite
        carta.AddComponent<SpriteRenderer>();
        SpriteRenderer imageCarta = carta.GetComponent<SpriteRenderer>();
        imageCarta.sprite = Resources.Load<Sprite>("Carte/" + nomeCarta);
        imageCarta.sortingOrder = layer++;
        //Imposta Posizione
        Transform posizioneCarta = carta.transform;
        
        switch (Pos)
        {
            case "0":
                posizioneCarta.position = new Vector3(pos0.position.x, pos0.position.y);
                posizioneCarta.position += Vector3.up * numCarte[0] * 0.6f;
                numCarte[0]++;
                break;

            case "1":
                posizioneCarta.position = new Vector3(pos1.position.x, pos1.position.y);
                posizioneCarta.position += Vector3.up * numCarte[1] * 0.6f;
                numCarte[1]++;
                break;

            case "2":
                posizioneCarta.position = new Vector3(pos2.position.x, pos2.position.y);
                posizioneCarta.position += Vector3.up * numCarte[2] * 0.6f;
                numCarte[2]++;
                break;

            case "3":
                posizioneCarta.position = new Vector3(pos3.position.x, pos3.position.y);
                posizioneCarta.position += Vector3.up * numCarte[3] * 0.6f;
                numCarte[3]++;
                break;

            case "4":
                posizioneCarta.position = new Vector3(pos4.position.x, pos4.position.y);
                posizioneCarta.position += Vector3.up * numCarte[4] * 0.6f;
                numCarte[4]++;
                break;

            case "5":
                posizioneCarta.position = new Vector3(pos5.position.x, pos5.position.y);
                posizioneCarta.position += Vector3.up * numCarte[5] * 0.6f;
                numCarte[5]++;
                break;

            case "6":
                posizioneCarta.position = new Vector3(pos6.position.x, pos6.position.y);
                posizioneCarta.position += Vector3.up * numCarte[6] * 0.6f;
                numCarte[0]++;
                break;

            case "dealer":
                posizioneCarta.position = new Vector3(posDealer.position.x, posDealer.position.y);
                posizioneCarta.position -= Vector3.right * numCarte[7];
                numCarte[7]++;
                break;


        }
    }

    public void requestMove()
    {
        Debug.Log("mossa richiesta");
        addBT.GetComponent<Button>().interactable = true;
        stayBT.GetComponent<Button>().interactable = true;
    }

    public void HiddenAdd()
    {
        GameObject carta = new GameObject();
        carta.tag = "Carta";
        listaCarte.Add(carta);
        //Aggiungi Sprite
        carta.AddComponent<SpriteRenderer>();
        SpriteRenderer imageCarta = carta.GetComponent<SpriteRenderer>();
        imageCarta.sprite = Resources.Load<Sprite>("Carte/BackColor_Red");
        imageCarta.sortingOrder = layer++;
        //Imposta Posizione
        Transform posizioneCarta = carta.transform;
        posizioneCarta.position = posDealer.position;
        posizioneCarta.position -= Vector3.right * numCarte[7];
    }

    public void addCarta()
    {
        TCPClient.Instance.InviaMessaggio("hit");
        addBT.GetComponent<Button>().interactable = false;
        stayBT.GetComponent<Button>().interactable = false;
    }

    public void stayCarta()
    {
        TCPClient.Instance.InviaMessaggio("stay");
        addBT.GetComponent<Button>().interactable = false;
        stayBT.GetComponent<Button>().interactable = false;
    }

    public void UpdateName(string[] nameStrings)
    {
        Debug.Log(nameStrings[1]);
        nameText1.text = nameStrings[1];
        nameText2.text = nameStrings[2];
        nameText3.text = nameStrings[3];
        nameText4.text = nameStrings[4];
        nameText5.text = nameStrings[5];
        nameText6.text = nameStrings[6];
        nameText7.text = nameStrings[7];
    }

    public void Quit()
    {
        TCPClient.Instance.InviaMessaggio("quit");
        SceneManager.LoadScene(1);
    }

    public void Bust(string client)
    {
        switch (client)
        {
            case "0":
                statoText1.text = "Bust";
                break;

            case "1":
                statoText2.text = "Bust";
                break;

            case"2":
                statoText3.text = "Bust";
                break;

            case "3":
                statoText4.text = "Bust";
                break;

            case "4":
                statoText5.text = "Bust";
                break;

            case "5":
                statoText6.text = "Bust";
                break;

            case "6":
                statoText7.text = "Bust";
                break;

            case "dealer":
                statoTextDealer.text = "Bust";
                break;

        }
    }

    public void BlackJack(string client)
    {
        switch (client)
        {
            case "0":
                statoText1.text = "BlackJack";
                break;

            case "1":
                statoText2.text = "BlackJack";
                break;

            case "2":
                statoText3.text = "BlackJack";
                break;

            case "3":
                statoText4.text = "BlackJack";
                break;

            case "4":
                statoText5.text = "BlackJack";
                break;

            case "5":
                statoText6.text = "BlackJack";
                break;

            case "6":
                statoText7.text = "BlackJack";
                break;

            case "dealer":
                statoTextDealer.text = "BlackJack";
                break;

        }
    }

    public void Results(string[] resultStrings)
    {
        statoTextDealer.text = "";
        statoText1.text = resultStrings[1];
        statoText2.text = resultStrings[2];
        statoText3.text = resultStrings[3];
        statoText4.text = resultStrings[4];
        statoText5.text = resultStrings[5];
        statoText6.text = resultStrings[6];
        statoText7.text = resultStrings[7];
    }
}
