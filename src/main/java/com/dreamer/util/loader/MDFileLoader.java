package com.dreamer.util.loader;

import com.dreamer.corpus.Chapter;
import com.dreamer.corpus.QuranObject;
import com.dreamer.corpus.Verse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MDFileLoader {

    public List<Chapter> loadTafsir(QuranObject quranObject, String file) {
        List<Chapter> chapters = new ArrayList<>(114);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            int globalVerseId = 0;
            boolean tafsirStarted = false;

            int chapterId = 1;
            int verseId = 0;

            Optional<Chapter> chapterRef;

            while (line != null) {
                if (line.startsWith("#")) {
                    tafsirStarted = true;
                    globalVerseId = Integer.parseInt(line.replace("# ", ""));
                } else if (tafsirStarted && !line.isEmpty()) {
                    chapterRef = findChapterByGlobalVerseId(quranObject, globalVerseId, chapterId);

                    if (chapterRef.isPresent()) {
                        if (chapterRef.get().getId() != chapterId) {
                            chapterId = chapterRef.get().getId();
                        }

                        if (chapterId > chapters.size()) {
                            Chapter chapter = new Chapter(chapterId,
                                    chapterRef.get().getName(), chapterRef.get().getVerseCount(),
                                    chapterRef.get().getStartingVerseId());
                            chapters.add(chapter);
                        }

                        verseId = globalVerseId - chapterRef.get().getStartingVerseId();


                        chapters.get(chapterId-1).getVerses().add(new Verse(chapterId, verseId, line));

                    }
                }

                // read next line
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return chapters;
    }

    private Optional<Chapter> findChapterByGlobalVerseId(QuranObject quranObject, int globalVerseId, int chapterIdSeed) {
        for (int i=chapterIdSeed-1; i <= 114; i++) {
            Chapter chapter = quranObject.getChapters().get(i);

            if (globalVerseId >= chapter.getStartingVerseId() && globalVerseId <= chapter.getEndingVerseId()) {
                return Optional.of(chapter);
            }
        }

        return Optional.empty();
    }
}
