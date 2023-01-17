using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameManager : MonoBehaviour
{
    [SerializeField] private Transform pos0, pos1, pos2, pos3, pos4, pos5, pos6, posDealer;
    int[] numCarte = new int[8];
    int layer = 1;

    void startGame()
    {

    }

    // Start is called before the first frame update
    void Start()
    {
        TCPClient.Instance.setGameManager(this);

        for(int i = 0; i < numCarte.Length; i++)
        {
            numCarte[i] = 0;
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Space))
        {
            addCarta("0", "Club01");
        }
        if (Input.GetKeyDown(KeyCode.W))
        {
            addCarta("dealer", "Club01");
        }
        if (Input.GetKeyDown(KeyCode.S))
        {
            HiddenAdd();
        }
    }

    public void addCarta(string Pos, string nomeCarta)
    {
        GameObject carta = new GameObject();
        //Aggiungi Sprite
        carta.AddComponent<SpriteRenderer>();
        SpriteRenderer imageCarta = carta.GetComponent<SpriteRenderer>();
        imageCarta.sprite = Resources.Load<Sprite>("Carte/"+nomeCarta);
        imageCarta.sortingOrder = layer++;
        //Imposta Posizione
        Transform posizioneCarta = carta.transform;
        switch (Pos)
        {
            case "0":
                posizioneCarta.position = pos0.position;
                posizioneCarta.position += Vector3.up * numCarte[0];
                numCarte[0]++;
                break;

            case "1":
                posizioneCarta.position = pos1.position;
                posizioneCarta.position += Vector3.up * numCarte[1];
                numCarte[1]++;
                break;

            case "2":
                posizioneCarta.position = pos2.position;
                posizioneCarta.position += Vector3.up * numCarte[2];
                numCarte[2]++;
                break;

            case "3":
                posizioneCarta.position = pos3.position;
                posizioneCarta.position += Vector3.up * numCarte[3];
                numCarte[3]++;
                break;

            case "4":
                posizioneCarta.position = pos4.position;
                posizioneCarta.position += Vector3.up * numCarte[4];
                numCarte[4]++;
                break;

            case "5":
                posizioneCarta.position = pos5.position;
                posizioneCarta.position += Vector3.up * numCarte[5];
                numCarte[5]++;
                break;

            case "6":
                posizioneCarta.position = pos6.position;
                posizioneCarta.position += Vector3.up * numCarte[6];
                numCarte[0]++;
                break;

            case "dealer":
                posizioneCarta.position = posDealer.position;
                posizioneCarta.position -= Vector3.right * numCarte[7];
                numCarte[7]++;
                break;

            
        }
    }

    public void HiddenAdd()
    {
        GameObject carta = new GameObject();
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
}
