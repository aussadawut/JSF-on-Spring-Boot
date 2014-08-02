package de.beyondjava.examples.scopes.beyondViewScope;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.faces.bean.CustomScoped;
import javax.faces.context.FacesContext;

public class BeyondViewScopeELResolver extends ELResolver {

	@Override
	public Object getValue(final ELContext elContext, final Object base, final Object property) {

		if (property == null) {
			throw new PropertyNotFoundException();
		}
		
		FacesContext facesContext = (FacesContext) elContext.getContext(FacesContext.class);

		if ((null == base) && BeyondViewScope.SCOPE_NAME.equals(property.toString())) {

			// Scope is referenced directly
			BeyondViewScope scope = getScope(facesContext);
			elContext.setPropertyResolved(true);
			return scope;

		} else if ((null != base) && (base instanceof BeyondViewScope)) {

			// An object within the scope is referenced

			return resolve(facesContext, (BeyondViewScope) base, property.toString());

		} else if (null == base) {
			BeyondViewScope customScope = getScope(facesContext);
			return null != customScope ? resolve(facesContext, customScope, property.toString()):null;

		}
		return null;
	}


	/**
	 * Resolve the key on the given {@link BeyondViewScope}
	 * @param facesContext
	 * @param scope
	 * @param key
	 * @return scoped
	 */
	public Object resolve(final FacesContext facesContext, final BeyondViewScope scope, final String key) {

		Object value = scope.get(key);
		facesContext.getELContext().setPropertyResolved(value != null);
		return value;

	}


	/**
	 * Responsible to retrieve the scope, or to create it if it doesn't exist yet.
	 * @param facesContext
	 * @return
	 */
	private BeyondViewScope getScope(final FacesContext facesContext) {

		Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
		BeyondViewScope customScope = (BeyondViewScope) sessionMap.get(BeyondViewScope.SCOPE_NAME);
		
		if (null == customScope) {
	        customScope = new BeyondViewScope();
	        sessionMap.put(BeyondViewScope.SCOPE_NAME, customScope);
	 
	        customScope.notifyCreate(facesContext);
		}

		return customScope;
	}


	@Override
	public Class<?> getType(final ELContext elContext, final Object base, final Object property) {
		return Object.class;
	}

	@Override
	public void setValue(final ELContext elContext, final Object base, final Object property, final Object value) {
		// do nothing
	}

	@Override
	public boolean isReadOnly(final ELContext elContext, final Object base, final Object property) {
		return true;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext elContext, final Object base) {
		return Collections.<FeatureDescriptor>emptyList().iterator();
	}

	@Override
	public Class<?> getCommonPropertyType(final ELContext elContext, final Object base) {
		if (base != null) {
			return null;
		}
		return String.class;
	}

}