package com.dreamer;

import com.dreamer.corpus.*;
import com.dreamer.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
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

    @FXML
    private ImageView vocabularyImage;


    private BookView quranView;


    private Optional<QuranObject> bookRef;

    private MediaPlayer mediaPlayer;

    @FXML
    private Button prevVerse;

    @FXML
    private Button nextVerse;

    @FXML
    private Button vocabularyPrevButton;

    @FXML
    private Button vocabularyNextButton;

//    @FXML
//    private Button tafsirJalalainPrevButton;
//
//    @FXML
//    private Button tafsirJalalainNextButton;

    @FXML
    private HTMLEditor similarVerses;

    @FXML
    private CheckBox playContinuous;

    @FXML
    private ToggleButton playButton;

    private boolean atEndOfMedia = true;

    private Optional<Range> playRange;

    private PlayInfo playInfo;
    int chapterId = 1;
    int verseId = 1;

//    @FXML
//    TabPane currentVerseTafsirPane;

    //TafsirPane tafsirPane;

    @FXML
    Button randomVerse;

    Pattern pattern = Pattern.compile("(\\d+)-?(\\d+)?");
    int currentPageId = 103;

    private PageModelImpl quranModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String resourcePath = System.getProperty("quranmemo.resource.path");

        quranModel = new PageModelImpl(Path.of(resourcePath + "page_images"));
        quranView = new BookView(quranModel, pageImage);

        try {
            bookRef = loader.loadMetaData(new FileInputStream(resourcePath + "quran-data.xml"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        bookRef.ifPresent(quranObject -> {
            loader.populateTranslationText(quranObject, resourcePath + "bn.bengali.txt");

            //tafsirPane = TafsirPane.build(currentVerseTafsirPane, loader.getMdFileLoader(), quranObject);
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

        pageIdInput.setOnAction(e -> {
            updatePageRange();

            List<Verse> selectedVerses = preparePlayList(playRange.get());
            playInfo.setVerses(selectedVerses);

            Range currentPageRange = buildVerseRange(bookRef.get(), currentPageId, 1);
            List<Verse> versesInPage = preparePlayList(currentPageRange);
            updatePageTranslationTab(versesInPage);

            updateMediaPlayer(bookRef.get());
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

        Range currentPageRange = buildVerseRange(bookRef.get(), currentPageId, 1);
        List<Verse> versesInPage = preparePlayList(currentPageRange);
        updatePageTranslationTab(versesInPage);
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

//        System.out.println("Chapter: " + playInfo.getCurrentVerse().getChapterId()
//                + ", Verse: " + playInfo.getCurrentVerse().getVerseId()
//                + ", page: " + currentPageId);

        Optional<Verse> translatedVerseRef = quranObject.getTranslatedChapter(playInfo.getCurrentVerse().getChapterId())
                .flatMap(chapter -> chapter.getVerse(verseId));

        String imageFileName = "verse_images/" + chapterId + "_" + verseId + ".png";
        verseImage.setImage(new Image(getClass().getResourceAsStream(imageFileName)));

        quranObject.getChapter(chapterId).ifPresent(chapter1 -> {
            chapterLabel.setText("Sura " + chapter1.getName());
        });

        translatedVerseRef.ifPresent(verse -> {
            verseLabel.setText("Verse: " + verseId);
            verseBengaliTranslation.setText(verse.getText());
        });

        //tafsirPane.updateView(playInfo.getCurrentVerse().getChapterId()-1, verseId);

        quranView.setCurrentPageIndex(currentPageId+1);

        if (playInfo.isPlayAudhubillah()) {
            soundFileName = String.format("verse_recitation/audhubillah.mp3", chapterId, verseId);
        } else if (playInfo.isPlayBismillah()) {
            soundFileName = String.format("verse_recitation/bismillah.mp3", chapterId, verseId);
        } else {
            soundFileName = String.format("verse_recitation/%03d%03d.mp3", chapterId, verseId);
        }

        if (playContinuous.isSelected() || playInfo.isPlayBismillah() || playInfo.isPlayAudhubillah()
                || playInfo.getCurrentVerseRepeated() < Integer.parseInt(repeatCountInput.getText())) {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            mediaPlayer = prepareMediaPlayer(getClass().getResource(soundFileName).toExternalForm());
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
        Optional<Page> page2Ref = quranObject.getPage(
                Math.min(startPageId+pageCount, quranObject.getPages().size()));

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
