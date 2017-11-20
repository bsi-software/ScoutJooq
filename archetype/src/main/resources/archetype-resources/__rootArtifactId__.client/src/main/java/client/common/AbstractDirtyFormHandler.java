#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.client.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.shared.AbstractIcons;

public abstract class AbstractDirtyFormHandler extends AbstractFormHandler {

	private static final String PROP_DIRTY_LISTENER = AbstractDirtyFormHandler.class.getName() + ".dirtyListener";

	@Override
	protected void execPostLoad() {
		installDirtyListener(getForm());
	}

	@Override
	protected void execFinally() {
		uninstallDirtyListener(getForm());
	}

	/**
	 * Method invoked once the dirty status of the {@link IForm} changes.
	 */
	protected void execDirtyStatusChanged(final boolean dirty) {
	}

	protected void installDirtyListener(final IForm form) {
		final Set<IFormField> dirtyFields = new HashSet<>();

		final PropertyChangeListener dirtyListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (form.isFormLoading()) {
					return;
				}

				// Check whether this field is dirty
				final IFormField formField = (IFormField) evt.getSource();
				formField.checkSaveNeeded();
				if (formField.isSaveNeeded()) {
					dirtyFields.add(formField);
				} else {
					dirtyFields.remove(formField);
				}

				final boolean dirty = dirtyFields.isEmpty();
				form.setIconId(dirty ? null : AbstractIcons.Pencil);

				try {
					execDirtyStatusChanged(dirty);
				} catch (final Exception e) {
					BEANS.get(ExceptionHandler.class).handle(e);
				}
			}
		};

		form.setProperty(PROP_DIRTY_LISTENER, dirtyListener);

		for (final IFormField field : form.getAllFields()) {
			field.addPropertyChangeListener(IValueField.PROP_DISPLAY_TEXT, dirtyListener);
		}
	}

	protected void uninstallDirtyListener(final IForm form) {
		final Object dirtyListener = form.getProperty(PROP_DIRTY_LISTENER);
		if (dirtyListener instanceof PropertyChangeListener) {
			form.removePropertyChangeListener((PropertyChangeListener) dirtyListener);
		}
	}
}
