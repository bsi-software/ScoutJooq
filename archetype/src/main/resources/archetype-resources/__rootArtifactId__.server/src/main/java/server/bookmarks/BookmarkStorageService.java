#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.server.bookmarks;

import javax.annotation.PostConstruct;

import org.eclipse.scout.rt.platform.ApplicationScoped;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.ObjectUtility;
import org.eclipse.scout.rt.server.services.common.bookmark.AbstractBookmarkStorageService;
import org.eclipse.scout.rt.shared.services.common.bookmark.BookmarkFolder;

import ${package}.server.ServerSession;

@Order(500)
@ApplicationScoped
public class BookmarkStorageService extends AbstractBookmarkStorageService {

    private BookmarkService m_service;

    @PostConstruct
    private void init() {
        m_service = BEANS.get(BookmarkService.class);
    }

    /**
     * This should be unique enough so no user has this as a username.
     */
    public static final String GLOBAL_BOOKMARKS_ID = "d1e38e9e-2c30-415c-baf1-7430803925b7";

    @Override
    protected Object getCurrentUserId() {
        return ServerSession.get().getUserId();
    }

    @Override
    protected BookmarkFolder readUserFolder(Object userId) {
        return m_service.getFolder(ObjectUtility.toString(userId));
    }

    @Override
    protected BookmarkFolder readGlobalFolder() {
        return m_service.getFolder(GLOBAL_BOOKMARKS_ID);
    }

    @Override
    protected void writeUserFolder(BookmarkFolder folder, Object userId) {
        m_service.storeFolder(folder, ObjectUtility.toString(userId));

    }

    @Override
    protected void writeGlobalFolder(BookmarkFolder folder) {
        m_service.storeFolder(folder, GLOBAL_BOOKMARKS_ID);
    }

}
