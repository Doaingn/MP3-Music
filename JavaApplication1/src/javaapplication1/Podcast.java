/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author zaqwe
 */
public class Podcast {
    private Mp3File mp3File;
    private ID3v2 id3v2Tag;
    private ArrayList<String> mp3Paths = new ArrayList<>();
    public Podcast(String filePath) throws IOException, UnsupportedTagException, InvalidDataException {
        mp3File = new Mp3File(filePath);
        if (mp3File.hasId3v2Tag()) {
            id3v2Tag = mp3File.getId3v2Tag();
        } else {
            // If no ID3v2 tag exists, create one
            id3v2Tag = new ID3v24Tag();
            mp3File.setId3v2Tag(id3v2Tag);
        }
    }
        public void setPodcastArt(String imagePath) throws IOException { 
        byte[] imageData = Files.readAllBytes(Paths.get(imagePath)); 
        id3v2Tag.setAlbumImage(imageData, "image/jpeg"); 
    }
    
    public void setPodcastTitle(String title) {
        id3v2Tag.setTitle(title);
    }
    
    public void setStoryteller(String artist) {
        id3v2Tag.setArtist(artist);
    }
    
    public byte[] getPodcastArt(){
        return id3v2Tag.getAlbumImage();
    }
    
    public String getPodcastTitle() {
        return id3v2Tag.getTitle();
    }
    
    public String getStoryteller() {
          return id3v2Tag.getArtist();
      }
    
    public void save(String outputPath) throws IOException, NotSupportedException {
        mp3File.save(outputPath);
    }  
}
