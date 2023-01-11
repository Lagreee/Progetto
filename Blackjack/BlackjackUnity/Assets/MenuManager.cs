using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.SceneManagement;
public class MenuManager : MonoBehaviour
{
    [SerializeField] TextMeshProUGUI nameText;
    [SerializeField] TextMeshProUGUI errorText;

    public void Play()
    {
        name = nameText.text;
        Debug.Log(name);

        if (TCPClient.Instance.ConnectToServer(name))
        {
            SceneManager.LoadScene(1);
        }
        else
        {
            errorText.text = "Can't connect to the server";
        }


    }

    public void Quit()
    {
        Application.Quit();
    }
}
