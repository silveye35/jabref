package org.jabref.logic.pdf.search.indexing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.LinkedFile;
import org.jabref.model.pdf.search.SearchFieldConstants;

import org.apache.lucene.document.Document;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class DocumentReaderTest {

    @Test
    public void unknownFileTestShouldReturnEmptyList() throws IOException {
        // given
        BibEntry entry = new BibEntry();
        entry.setFiles(Collections.singletonList(new LinkedFile("Wrong path", "src/test/resources/pdfs/NOT_PRESENT.pdf", "Type")));

        // when
        final List<Document> emptyDocumentList = new DocumentReader(entry).readLinkedPdfs();

        // then
        assertEquals(Collections.emptyList(), emptyDocumentList);
    }

    @Test(expected = IllegalStateException.class)
    public void noLinkedFiles() throws IOException {
        // given
        BibEntry entry = new BibEntry();

        // when
        new DocumentReader(entry);
    }


    @Test
    public void exampleTest() throws IOException {
        // given
        BibEntry entry = new BibEntry("article");
        entry.setCiteKey("Example2017");
        entry.setFiles(Collections.singletonList(new LinkedFile("Example", "src/test/resources/pdfs/example.pdf", "pdf")));

        // when
        List<Document> documents = new DocumentReader(entry).readLinkedPdfs();

        // then
        assertEquals(1, documents.size());

        Document doc = documents.get(0);
        assertEquals("Example2017", doc.get(SearchFieldConstants.KEY));
        assertFalse(doc.get(SearchFieldConstants.CONTENT).isEmpty());
        assertEquals("LaTeX with hyperref package", doc.get(SearchFieldConstants.CREATOR));
    }

    @Test
    public void thesisExampleTest() throws IOException {
        // given
        BibEntry entry = new BibEntry("PHDThesis");
        entry.setCiteKey("ThesisExample2017");
        entry.setFiles(Collections.singletonList(new LinkedFile("Example thesis", "src/test/resources/pdfs/thesis-example.pdf", "pdf")));

        // when
        List<Document> documents = new DocumentReader(entry).readLinkedPdfs();

        //then
        assertEquals(1, documents.size());

        Document doc = documents.get(0);
        assertEquals("ThesisExample2017", doc.get(SearchFieldConstants.KEY));
        assertFalse(doc.get(SearchFieldConstants.CONTENT).isEmpty());
        assertEquals("LaTeX, hyperref, KOMA-Script", doc.get(SearchFieldConstants.CREATOR));
    }

    @Test
    public void minimalTest() throws IOException {
        // given
        BibEntry entry = new BibEntry("article");
        entry.setCiteKey("Minimal2017");
        entry.setFiles(Collections.singletonList(new LinkedFile("Example thesis", "src/test/resources/pdfs/minimal.pdf", "pdf")));

        // when
        List<Document> documents = new DocumentReader(entry).readLinkedPdfs();

        // then
        assertEquals(1, documents.size());

        Document doc = documents.get(0);
        assertEquals("Minimal2017", doc.get(SearchFieldConstants.KEY));
        assertEquals("Hello World\n1\n", doc.get(SearchFieldConstants.CONTENT));
        assertEquals("TeX", doc.get(SearchFieldConstants.CREATOR));
    }

    @Test
    public void metaDataTest() throws IOException {
        // given
        BibEntry entry = new BibEntry();
        entry.setCiteKey("MetaData2017");
        entry.setFiles(Collections.singletonList(new LinkedFile("Minimal", "src/test/resources/pdfs/metaData.pdf", "pdf")));

        // when
        List<Document> documents = new DocumentReader(entry).readLinkedPdfs();

        // then
        assertEquals(1, documents.size());

        Document doc = documents.get(0);
        assertEquals("MetaData2017", doc.get(SearchFieldConstants.KEY));
        assertEquals("Test\n", doc.get(SearchFieldConstants.CONTENT));
        assertEquals("Author Name", doc.get(SearchFieldConstants.AUTHOR));
        assertEquals("pdflatex", doc.get(SearchFieldConstants.CREATOR));
        assertEquals("A Subject", doc.get(SearchFieldConstants.SUBJECT));
        assertEquals("Test, Whatever, Metadata, JabRef", doc.get(SearchFieldConstants.KEYWORDS));
    }
}