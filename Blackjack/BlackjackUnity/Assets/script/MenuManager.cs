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
        if (!string.Equals(name, "â??"))
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
