package victor.forum;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.List;

/**
 * Created by hp on 2016-07-07.
 */
public class Message implements OnSuccessListener<Uri>{

    private String mesage;
    private List<String> reponses;
    private int nbReponses;
    private double longitude;
    private double latitude;
    private String mediaUrl;
    private String key;
    private String idUser;

    public Message() {
    }

    public Message(String idUser, String mesage, String mediaUrl, double longitude, double latitude) {
        this.idUser = idUser;
        this.setMesage(mesage);
        this.setMediaUrl(mediaUrl);
        this.longitude = longitude;
        this.latitude = latitude;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<String> getReponses() {
        return reponses;
    }

    public void setReponses(List<String> reponses) {
        this.reponses = reponses;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMesage() {
        return mesage;
    }

    public void setMesage(String mesage) {
        this.mesage = mesage;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    @Override
    public void onSuccess(Uri u) {
        mediaUrl = u.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNbReponses() {
        return nbReponses;
    }

    public void setNbReponses(int nbReponses) {
        this.nbReponses = nbReponses;
    }
}
