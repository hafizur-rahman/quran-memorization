package com.dreamer;

import com.dreamer.corpus.*;
import com.dreamer.util.*;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    private QuranTextLoader loader = new QuranTextLoader();

    @FXML
    private TextField pageIdInput;

    @FXML
    private TextField repeatCountInput;

    @FXML
    private Label chapterLabel;

    @FXML
    private Label verseLabel;

    @FXML
    private Text verseBengaliTranslation;

    @FXML
    private Text pageBengaliTranslation;

    @FXML
    private ImageView verseImage;

    @FXML
    private ImageView pageImage;

    private BookView quranView;


    private Optional<QuranObject> bookRef;

    private MediaPlayer mediaPlayer;

    @FXML
    private Button prevVerse;

    @FXML
    private Button nextVerse;

    @FXML
    private Button prevPage;

    @FXML
    private Button nextPage;

    @FXML
    private ToggleButton showDetail;

    @FXML
    private BorderPane mainPane;

    @FXML
    private BorderPane detailPane;

    @FXML
    private CheckBox playContinuous;

    @FXML
    private ToggleButton playButton;

    private boolean atEndOfMedia = true;

    private Optional<Range> playRange;

    private PlayInfo playInfo;
    int chapterId = 1;
    int verseId = 1;

    @FXML
    Text verseSyntax;

    @FXML
    Text verseTafsir;

    @FXML
    Button randomVerse;

    Pattern pattern = Pattern.compile("(\\d+)-?(\\d+)?");
    int currentPageId = 318;

    private PageModelImpl quranModel;

    private String resourcePath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourcePath = System.getProperty("quranmemo.resource.path");

        quranModel = new PageModelImpl(resourcePath + "\\page_images");
        quranView = new BookView(quranModel, pageImage);

        try {
            bookRef = loader.loadMetaData(new FileInputStream(resourcePath + "\\quran-data.xml"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        bookRef.ifPresent(quranObject -> {
            loader.populateTranslationText(quranObject, resourcePath + "\\bn.bengali.txt");
            loader.populateSyntaxText(quranObject, resourcePath + "\\verse_syntax.txt");
        });

        updatePageRange();

        List<Verse> verses = preparePlayList(playRange.get());
        playInfo = new PlayInfo(playRange.get(), verses);
        playInfo.updateRepeatCount(Integer.parseInt(repeatCountInput.getText()));

        pageIdInput.setText(Integer.toString(currentPageId));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (atEndOfMedia && playButton.isSelected()) {
                            bookRef.ifPresent(new Consumer<QuranObject>() {
                                @Override
                                public void accept(QuranObject quranObject) {
                                    Platform.runLater(() -> {
                                        updateMediaPlayer(quranObject);
                                    });
                                }
                            });
                        }
                    }
                },
                1000,
                1000
        );

        pageIdInput.setOnAction(e-> {
            updatePageView();
        });

        prevPage.setOnAction(e -> {
            if (currentPageId > 1) {
                currentPageId --;
                pageIdInput.setText(Integer.toString(currentPageId));

                updatePageView();
            }
        });

        nextPage.setOnAction(e -> {
            if (currentPageId < 610) {
                currentPageId ++;
                pageIdInput.setText(Integer.toString(currentPageId));

                updatePageView();
            }
        });

        repeatCountInput.setOnAction(e -> {
            playInfo.updateRepeatCount(Integer.parseInt(repeatCountInput.getText()));
        });

        playButton.setOnAction(e -> {
            if (mediaPlayer == null) {
                if (playButton.isSelected()) {
                    updateMediaPlayer(bookRef.get());
                }
            } else {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (playButton.isSelected()
                        || status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }

                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });

        prevVerse.setOnAction(e -> {
            playInfo.moveToPrevVerse();

            updateMediaPlayer(bookRef.get());
        });

        nextVerse.setOnAction(e -> {
            playInfo.moveToNextVerse();

            updateMediaPlayer(bookRef.get());
        });

        randomVerse.setOnAction(e -> {
            playInfo.moveToRandomVerse();

            updateMediaPlayer(bookRef.get());
        });

        showDetail.setOnAction(e -> {
            detailPane.setVisible(showDetail.isSelected());

            if (showDetail.isSelected()) {
                mainPane.getScene().getWindow().sizeToScene();
            } else {
                mainPane.getScene().getWindow().setWidth(560);
            }
        });

        Range currentPageRange = buildVerseRange(bookRef.get(), currentPageId, 1);
        List<Verse> versesInPage = preparePlayList(currentPageRange);
        updatePageTranslationTab(versesInPage);
    }

    private void updatePageView() {
        updatePageRange();

        List<Verse> selectedVerses = preparePlayList(playRange.get());
        playInfo.setVerses(selectedVerses);

        Range currentPageRange = buildVerseRange(bookRef.get(), currentPageId, 1);
        List<Verse> versesInPage = preparePlayList(currentPageRange);
        updatePageTranslationTab(versesInPage);

        updateMediaPlayer(bookRef.get());
    }

    private void updatePageTranslationTab(List<Verse> verses) {
        StringBuilder pageTranslation = new StringBuilder();

        for (Verse verse: verses) {
            int verseId = verse.getVerseId();

            if (verseId == 1) {
                bookRef.get().getChapter(verse.getChapterId()).ifPresent(chapter -> {
                    pageTranslation.append("\n\n Sura ").append(chapter.getName()).append("\n");
                });
            }

            pageTranslation.append("(").append(verseId).append(") ").append(verse.getText()).append(" ");
        }
        pageBengaliTranslation.setText(pageTranslation.toString());
    }

    private List<Verse> preparePlayList(Range pageRange) {
        List<Verse> verses = new ArrayList<>();

        bookRef.ifPresent(quranObject -> {
            for (int i = pageRange.getStartVerse().getChapterId(); i <= pageRange.getEndVerse().getChapterId(); i++) {
                boolean isStartingChapter = (i == pageRange.getStartVerse().getChapterId());
                boolean isEndingChapter = (i == pageRange.getEndVerse().getChapterId());

                if (isStartingChapter) {
                    quranObject.getChapter(i).ifPresent(chapter -> {
                        int startIndex = pageRange.getStartVerse().getVerseId()-1;
                        int endIndex = isEndingChapter ?
                                Math.min(pageRange.getEndVerse().getVerseId(), chapter.getVerses().size()) : chapter.getVerses().size();

                        verses.addAll(chapter.getVerses().subList(startIndex, endIndex));
                    });
                } else if (isEndingChapter) {
                    quranObject.getChapter(i).ifPresent(chapter -> {
                        int endIndex = Math.min(pageRange.getEndVerse().getVerseId(), chapter.getVerses().size());

                        verses.addAll(chapter.getVerses().subList(0, endIndex));
                    });
                } else {
                    quranObject.getChapter(i).ifPresent(chapter -> {
                        verses.addAll(chapter.getVerses());
                    });
                }
            }
        });

        return verses;
    }

    private void updateMediaPlayer(QuranObject quranObject) {
        if (playInfo.isRangeEnded()) {
            playInfo.reset();
        }

        String soundFileName = null;

        chapterId = playInfo.getCurrentVerse().getChapterId();
        verseId = playInfo.getCurrentVerse().getVerseId();

        Optional<Page> currentPage = quranObject.locatePageByVerse(playInfo.getCurrentVerse());
        if (currentPageId != currentPage.get().getIndex()) {
            currentPageId = currentPage.get().getIndex();

            Range currentPageRange = buildVerseRange(bookRef.get(), currentPageId, 1);
            List<Verse> versesInPage = preparePlayList(currentPageRange);
            updatePageTranslationTab(versesInPage);
        }

        Optional<Verse> translatedVerseRef = quranObject.getTranslatedChapter(playInfo.getCurrentVerse().getChapterId())
                .flatMap(chapter -> chapter.getVerse(verseId));

        String imageFileName = "verse_images/" + chapterId + "_" + verseId + ".png";
        try {
            verseImage.setImage(new Image(new FileInputStream(resourcePath + "\\" + imageFileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        quranObject.getChapter(chapterId).ifPresent(chapter1 -> {
            chapterLabel.setText("Sura " + chapter1.getName());
        });

        translatedVerseRef.ifPresent(verse -> {
            verseLabel.setText("Verse: " + verseId);
            verseBengaliTranslation.setText(verse.getText());
        });

        Optional<Verse> syntaxVerseRef = quranObject.getSyntaxChapter(playInfo.getCurrentVerse().getChapterId())
                .flatMap(chapter -> chapter.getVerse(verseId));

        syntaxVerseRef.ifPresent(verse -> {
            verseSyntax.setText(verse.getText());
        });

        verseTafsir.setText(getTafsir(chapterId, verseId));

        quranView.setCurrentPageIndex(currentPageId+1);

        if (playInfo.isPlayAudhubillah()) {
            soundFileName = String.format("verse_recitation\\audhubillah.mp3", chapterId, verseId);
        } else if (playInfo.isPlayBismillah()) {
            soundFileName = String.format("verse_recitation\\bismillah.mp3", chapterId, verseId);
        } else {
            soundFileName = String.format("verse_recitation\\%03d%03d.mp3", chapterId, verseId);
        }

        if (playContinuous.isSelected() || playInfo.isPlayBismillah() || playInfo.isPlayAudhubillah()
                || playInfo.getCurrentVerseRepeated() < Integer.parseInt(repeatCountInput.getText())) {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            mediaPlayer = prepareMediaPlayer(Path.of(resourcePath, soundFileName).toUri().toString());
            atEndOfMedia = false;
        }
    }

    private String getTafsir(int chapterId, int verseId) {
        var url = "jdbc:sqlite:"+ resourcePath + "/dbs/bn_tafsirzakaria.db";
        var sql = "SELECT text FROM verses WHERE sura=? and ayah=?";

        try (Connection conn = DriverManager.getConnection(url); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chapterId);
            stmt.setInt(2, verseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tafsir = rs.getString("text");

                    return tafsir;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void updatePageRange() {
        final Matcher matcher = pattern.matcher(pageIdInput.getText());

        playRange = bookRef.map(quranObject -> {
            int startPageId = currentPageId;
            int pageCount = 1;

            if(matcher.matches()) {
                startPageId = Integer.parseInt(matcher.group(1));

                if (matcher.group(2) != null) {
                    int endPageId = Integer.parseInt(matcher.group(2));
                    if (endPageId > startPageId) {
                        pageCount = endPageId - startPageId + 1;
                    }
                }

                currentPageId = startPageId;
            }

            return buildVerseRange(quranObject, startPageId, pageCount);
        });
    }

    private static Range buildVerseRange(QuranObject quranObject, int startPageId, int pageCount) {
        Page page = quranObject.getPage(startPageId).get();
        Optional<Page> page2Ref = quranObject.getPage(startPageId+pageCount);

        Optional<Verse> startVerse = quranObject.getChapter(page.getChapterId()).flatMap(
                chapter -> chapter.getVerse(page.getStartVerse()));

        Optional<Verse> endVerse = null;
        if (page2Ref.isPresent()) {
            if (page2Ref.get().getStartVerse() == 1) {
                endVerse = quranObject.getChapter(page2Ref.get().getChapterId()-1).map(chapter ->
                        chapter.getVerses().getLast());
            } else {
                endVerse = quranObject.getChapter(page2Ref.get().getChapterId()).map(chapter ->
                        chapter.getVerse(page2Ref.get().getStartVerse()-1).get());
            }
        } else {
            endVerse = Optional.of(quranObject.getChapters().getLast().getVerses().getLast());
        }

        return new Range(startVerse.get(), endVerse.get());
    }

    private MediaPlayer prepareMediaPlayer(String url) {
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(url));
        mediaPlayer.play();

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (!playButton.isSelected()) {
                    mediaPlayer.pause();
                } else {
                    playButton.setText("||");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                //System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            atEndOfMedia = true;
            playButton.setText(">");

            if (playInfo.isPlayAudhubillah()) {
                playInfo.setPlayAudhubillah(false);
            } else if (playInfo.isPlayBismillah()) {
                playInfo.setPlayBismillah(false);
            } else {
                playInfo.incrementCurrentVerseRepeatCount();

                if (playContinuous.isSelected()
                        && playInfo.getCurrentVerseRepeated() >= Integer.parseInt(repeatCountInput.getText())) {
                    playInfo.moveToNextVerseWithRepeating();
                }
            }
        });

        return mediaPlayer;
    }
}
