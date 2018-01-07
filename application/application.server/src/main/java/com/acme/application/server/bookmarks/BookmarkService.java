package com.acme.application.server.bookmarks;

import java.io.IOException;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.serialization.SerializationUtility;
import org.eclipse.scout.rt.shared.services.common.bookmark.BookmarkFolder;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.or.core.tables.Bookmark;
import com.acme.application.database.or.core.tables.records.BookmarkRecord;
import com.acme.application.server.common.AbstractBaseService;

public class BookmarkService extends AbstractBaseService<Bookmark, BookmarkRecord> {

    public static final Logger LOG = LoggerFactory.getLogger(BookmarkService.class);

    @Override
    public Bookmark getTable() {
        return Bookmark.BOOKMARK;
    }

    @Override
    public Field<String> getIdColumn() {
        return Bookmark.BOOKMARK.USER;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

    public BookmarkFolder getFolder(String id) {
        BookmarkRecord bookmarkRecord = get(id);
        if (bookmarkRecord == null) {
            BookmarkFolder folder = new BookmarkFolder();
            return folder;
        }
        byte[] data = bookmarkRecord.getData();
        try {
            BookmarkFolder bookmarkFolder = SerializationUtility.createObjectSerializer().deserialize(data,
                    BookmarkFolder.class);
            return bookmarkFolder;
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("Error deserializing Bookmarks for user {}", id);
            throw new ProcessingException("Error deserializing Bookmarks", e);
        }
    }

    public void storeFolder(BookmarkFolder folder, String id) {
        try {
            byte[] data = SerializationUtility.createObjectSerializer().serialize(folder);
            BookmarkRecord record = new BookmarkRecord(id, data);
            store(id, record);
        } catch (IOException e) {
            LOG.error("Error serializing Bookmarks for user {}", id);
            throw new ProcessingException("Error serializing Bookmarks", e);
        }
    }

}
