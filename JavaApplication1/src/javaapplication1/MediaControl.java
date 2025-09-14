package javaapplication1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javazoom.jl.decoder.Header;
import com.mpatric.mp3agic.*;
import java.util.Collections;

public class MediaControl {
   private String directoryPath;
    private AdvancedPlayer player;
    private Thread playThread;
    private int pausedFrame;
    private boolean isPlaying;
    private boolean isPaused;
    private List<File> mp3Files;
    private int currentIndex;
    private long totalDuration;
    private long currentTime;
    private Thread timeThread;
    private int currentFrame = 0;
    private Music currentMusic;
    private Podcast currentPodcast;
    
    public MediaControl(String directoryPath) {
        this.directoryPath = directoryPath;
        this.pausedFrame = 0;
        this.isPlaying = false;
        this.isPaused = false;
        loadMp3Files();
    }

    public void loadMp3Files() {
        File dir = new File(directoryPath);
        if (dir.exists() && dir.isDirectory()) {
            mp3Files = new ArrayList<>(Arrays.asList(dir.listFiles((d, name) -> name.endsWith(".mp3"))));
            currentIndex = 0;
        } else {
            System.out.println("Directory not found!");
        }
    }

        public void pause() {
            if (player != null) {
                isPaused = true;
                pausedFrame = currentFrame;  // Store the current frame when paused
                player.close();
            }
}


    public void stop() {
        if (player != null) {
            player.close();
            playThread.interrupt();
            isPlaying = false;
            isPaused = false;
            pausedFrame = 0;
        }
    }

    public void next() {
        if (mp3Files != null && !mp3Files.isEmpty()) {
            stop();
            currentIndex = (currentIndex + 1) % mp3Files.size();
            startPlayingFromFile(mp3Files.get(currentIndex));
        }
    }

    public void previous() {
        if (mp3Files != null && !mp3Files.isEmpty()) {
            stop();
            currentIndex = (currentIndex - 1 + mp3Files.size()) % mp3Files.size();
            startPlayingFromFile(mp3Files.get(currentIndex));
        }
    }



        public void resumeOrStart() {
            if (isPaused) {
                startPlayingFromFrame(pausedFrame);  // Resume from the paused position
            } else {
                startPlayingFromFile(mp3Files.get(currentIndex));  // Start playing from the beginning
            }
            isPaused = false;
        }
        
    public void shuffle() {
        if (mp3Files != null && !mp3Files.isEmpty()) {
            // สุ่มลำดับเพลง
            Collections.shuffle(mp3Files);

            // รีเซ็ต index ให้เริ่มที่เพลงแรก
            currentIndex = 0;

            // แสดงรายชื่อเพลงหลังการ Shuffle (Optional)
            System.out.println("Playlist shuffled! New order:");
            for (File file : mp3Files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No songs available to shuffle!");
        }
}



private void startPlayingFromFile(File file) {
    try {
        // สร้าง Music สำหรับไฟล์ที่กำลังเล่น
        currentMusic = new Music(file.getAbsolutePath());
        System.out.println("Now playing: " + currentMusic.getMusicTitle());
        
        currentPodcast = new Podcast(file.getAbsolutePath());
        System.out.println("Now playing: " + currentPodcast.getPodcastTitle());
        
        // เริ่มเล่นเพลง
        playThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(file)) {
                player = new AdvancedPlayer(fis);

                // ติดตามตำแหน่ง
                Thread positionTracker = new Thread(() -> {
                    while (isPlaying && !isPaused) {
                        try {
                            Thread.sleep(100);  // อัปเดตทุกๆ 100ms
                            currentFrame++;  // เพิ่มตำแหน่งเฟรม
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                positionTracker.start();

                // เล่นเพลงตั้งแต่ต้น
                player.play(0, Integer.MAX_VALUE);
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        });
        playThread.start();
        isPlaying = true;
    } catch (IOException | UnsupportedTagException | InvalidDataException e) {
        e.printStackTrace();
    }
}


private void startPlayingFromFrame(int frame) {
    playThread = new Thread(() -> {
        try (FileInputStream fis = new FileInputStream(mp3Files.get(currentIndex))) {
            player = new AdvancedPlayer(fis);

            // Start tracking the position
            Thread positionTracker = new Thread(() -> {
                while (isPlaying && !isPaused) {
                    try {
                        Thread.sleep(100);  // Update position every 100ms
                        currentFrame++;  // Increment the frame position
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            positionTracker.start();

            // Play from the paused position
            player.play(frame, Integer.MAX_VALUE);
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    });
    playThread.start();
    isPlaying = true;
}


public String uploadMp3File() {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Files", "mp3");
    fileChooser.setFileFilter(filter);
    int returnValue = fileChooser.showOpenDialog(null);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        File destDir = new File(directoryPath);
        File destFile = new File(destDir, selectedFile.getName());

        try {
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mp3Files.add(destFile);
            System.out.println("File uploaded successfully!");
            return selectedFile.getName(); // คืนค่าชื่อไฟล์ที่อัปโหลด
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return null; // คืนค่า null หากไม่มีการเลือกไฟล์
}

    public Music getCurrentMusic() {
        return currentMusic; // ให้ตัวแปร currentMusic ใน MediaControl สามารถเข้าถึงได้
}
   
       public Podcast getCurrentPodcast() {
        return currentPodcast; // ให้ตัวแปร currentMusic ใน MediaControl สามารถเข้าถึงได้
    }
}