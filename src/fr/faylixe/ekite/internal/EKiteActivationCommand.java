package fr.faylixe.ekite.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import fr.faylixe.ekite.EKitePlugin;

/**
 * Command for activating / desactivating eKite.
 * 
 * @author fv
 */
public final class EKiteActivationCommand extends AbstractHandler  {

	/** {@inheritDoc} **/
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final EKitePlugin plugin = EKitePlugin.getDefault();
		if (plugin.isActive()) {
			plugin.desactivate();
		}
		else {
			plugin.activate();
		}
		return null;
	}

}
