package com.dreamer;

import com.dreamer.corpus.*;
import com.dreamer.ui.VerseAnalysisController;
import com.dreamer.ui.TafsirController;
import com.dreamer.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
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
    private TabPane tafsirPane;

    private TafsirController tafsirController;

    private VerseAnalysisController verseAnalysisController;

    @FXML
    Button randomVerse;

    Pattern pattern = Pattern.compile("(\\d+)-?(\\d+)?");
    int currentPageId = 1;

    private PageModelImpl quranModel;

    private String resourcePath;

    private AppConfig appConfig;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourcePath = System.getProperty("quranmemo.resource.path");

        loadConfig();

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

        tafsirController = new TafsirController(resourcePath, tafsirPane);
        verseAnalysisController = new VerseAnalysisController(bookRef, resourcePath, tafsirPane);

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

    }

    private void updatePageView() {
        updatePageRange();

        List<Verse> selectedVerses = preparePlayList(playRange.get());
        playInfo.setVerses(selectedVerses);

        updateMediaPlayer(bookRef.get());
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
        }

        Optional<Verse> translatedVerseRef = quranObject.getTranslatedChapter(playInfo.getCurrentVerse().getChapterId())
                .flatMap(chapter -> chapter.getVerse(verseId));

        String imageFileName = "verse_images/" + chapterId + "_" + verseId + ".png";
        try {
            javafx.scene.image.Image image = new javafx.scene.image.Image(new FileInputStream(resourcePath + "\\" + imageFileName));
            verseImage.setImage(image);
            //verseImage2.setImage(image);
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

        tafsirController.updateUI(chapterId, verseId);
        verseAnalysisController.updateUI(chapterId, verseId);

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

    public void saveConfig() {
        appConfig.setPageId(currentPageId);

        try (FileWriter f = new FileWriter(new File(resourcePath + File.separator + "config.json"))) {
            objectMapper.writeValue(f, appConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConfig() {
        try (FileInputStream f = new FileInputStream(new File(resourcePath + File.separator + "config.json"))) {
            appConfig = objectMapper.readValue(f, AppConfig.class);
        } catch (Exception e) {
            appConfig = new AppConfig();
            appConfig.setPageId(1);
        }

        currentPageId = appConfig.getPageId();
    }
}
