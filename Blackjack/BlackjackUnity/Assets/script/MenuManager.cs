using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;
public class MenuManager : MonoBehaviour
{
    [SerializeField] TextMeshProUGUI nameText;
    [SerializeField] TextMeshProUGUI IPText;
    [SerializeField] TextMeshProUGUI errorText;
    private string baseName;
    string IP;

    private void Start()
    {
        baseName = nameText.text;
    }


    public void Play()
    {
        name = nameText.text;
        IP = IPText.text;
        IP = IP.Remove(IP.Length - 1, 1);
        if (!string.Equals(name, baseName))
        {
            if (TCPClient.Instance.ConnectToServer(name, IP))
            {
                //SceneManager.LoadScene(1);
            }
            else
            {
                errorText.text = "Can't connect to the server";
            }
        }
        else
        {
            errorText.text = "Inserire un username";
        }


    }

    public void Quit()
    {
        Application.Quit();
    }
}
