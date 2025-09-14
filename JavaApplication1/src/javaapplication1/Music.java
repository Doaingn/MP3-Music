package javaapplication1;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author zaqwe
 */
import com.mpatric.mp3agic.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Music {
    private Mp3File mp3File;
    private ID3v2 id3v2Tag;
    private ArrayList<String> mp3Paths = new ArrayList<>();
    public Music(String filePath) throws IOException, UnsupportedTagException, InvalidDataException {
        mp3File = new Mp3File(filePath);
        if (mp3File.hasId3v2Tag()) {
            id3v2Tag = mp3File.getId3v2Tag();
        } else {
            // If no ID3v2 tag exists, create one
            id3v2Tag = new ID3v24Tag();
            mp3File.setId3v2Tag(id3v2Tag);
        }
    }
    
    public void setMusicAlbumArt(String imagePath) throws IOException { 
        byte[] imageData = Files.readAllBytes(Paths.get(imagePath)); 
        id3v2Tag.setAlbumImage(imageData, "image/jpeg"); 
    }
    
    public void setMusicTitle(String title) {
        id3v2Tag.setTitle(title);
    }
    
    public void setMusicArtist(String artist) {
        id3v2Tag.setArtist(artist);
    }

    public void setMusicAlbum(String album) {
        id3v2Tag.setAlbum(album);
    }

    public void setMusicGenre(String genre) {
        id3v2Tag.setGenreDescription(genre);
    }

    
    public byte[] getMusicAlbumArt(){
        return id3v2Tag.getAlbumImage();
    }
    
    public String getMusicTitle() {
        return id3v2Tag.getTitle();
    }
    
    public String getMusicArtist() {
          return id3v2Tag.getArtist();
      }
    
    public String getMusicAlbum() {
        return id3v2Tag.getAlbum();
    }

    public String getMusicGenre() {
        return id3v2Tag.getGenreDescription();
    }

    public void save(String outputPath) throws IOException, NotSupportedException {
        mp3File.save(outputPath);
    }  
}