using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;
public class MenuManager : MonoBehaviour
{
    [SerializeField] TextMeshProUGUI nameText;
    [SerializeField] TextMeshProUGUI errorText;
    private string baseName;

    private void Start()
    {
        baseName = nameText.text;
    }


    public void Play()
    {
        name = nameText.text;
        if (!string.Equals(name, baseName))
        {
            if (TCPClient.Instance.ConnectToServer(name))
            {
                SceneManager.LoadScene(1);
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
