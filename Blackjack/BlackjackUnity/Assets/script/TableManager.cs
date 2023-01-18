using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;

public class TableManager : MonoBehaviour
{
    [SerializeField] TextMeshProUGUI nameText;
    [SerializeField] TextMeshProUGUI errorText;

    [SerializeField] TextMeshProUGUI tavolo1;
    [SerializeField] TextMeshProUGUI StatoTavolo1;
    
    [SerializeField] TextMeshProUGUI tavolo2;
    [SerializeField] TextMeshProUGUI StatoTavolo2;
    
    [SerializeField] TextMeshProUGUI tavolo3;
    [SerializeField] TextMeshProUGUI StatoTavolo3;

    void Awake(){
        nameText.text = "Giocatore: " + TCPClient.Instance.getName();
        TCPClient.Instance.setTm(this);
        TCPClient.Instance.InviaMessaggio("getInfoTavoli");
    }



    void Start()
    {
        StartCoroutine(SendMessage());
    }

    IEnumerator SendMessage()
    {
        while (true)
        {
            TCPClient.Instance.InviaMessaggio("getInfoTavoli");
            yield return new WaitForSeconds(5f);
        }
    }

    public void Quit()
    {
        TCPClient.Instance.Quit();
        SceneManager.LoadScene(0);
    }


    public void SetUITavolo1(string giocatori, string inGame){
        tavolo1.text = "Tavolo 1: " + giocatori;
        StatoTavolo1.text  = "In Gioco: "+ inGame;
    }

    public void SetUITavolo2(string giocatori, string inGame){
        tavolo2.text = "Tavolo 2: " + giocatori;
        StatoTavolo2.text = "In Gioco: "+ inGame;
    }

    public void SetUITavolo3(string giocatori, string inGame){
        tavolo3.text = "Tavolo 3: " + giocatori;
        StatoTavolo3.text  = "In Gioco: "+ inGame;
    }

    public void TryConnectTavolo1(){
        TCPClient.Instance.connectToTable("Tavolo1");
    }
    public void TryConnectTavolo2(){
        TCPClient.Instance.connectToTable("Tavolo2");
    }
    public void TryConnectTavolo3(){
        TCPClient.Instance.connectToTable("Tavolo3");
    }
}
