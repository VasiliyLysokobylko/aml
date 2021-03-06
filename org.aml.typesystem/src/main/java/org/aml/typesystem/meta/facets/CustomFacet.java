package org.aml.typesystem.meta.facets;

import java.util.Set;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.ITypeRegistry;
import org.aml.typesystem.Status;
import org.aml.typesystem.beans.ISimpleFacet;
import org.aml.typesystem.meta.TypeInformation;

/**
 * <p>CustomFacet class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class CustomFacet extends TypeInformation implements ISimpleFacet {

	protected String name;

	protected Object value;

	/**
	 * <p>Constructor for CustomFacet.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Object} object.
	 */
	public CustomFacet(String name, Object value) {
		super(true);
		this.name = name;
		this.value = value;
	}

	/** {@inheritDoc} */
	@Override
	public String facetName() {
		return name;
	}
	/** {@inheritDoc} */
	@Override
	public void setValue(Object vl) {
		this.value=vl;
	}
	

	/** {@inheritDoc} */
	@Override
	public Status validate(ITypeRegistry registry) {
		final Set<CustomFacet> cf = this.ownerType.meta(CustomFacet.class);
		for (final CustomFacet c : cf) {
			if (c != this && c.name.equals(this.name)) {
				if (c.value != null && !c.value.equals(this.value)) {
					return new Status(Status.ERROR, 1, "user defined facets can not be redefined");
				}
			}
		}
		final Set<FacetDeclaration> meta = this.ownerType.meta(FacetDeclaration.class);
		boolean found=false;
		for (final FacetDeclaration d : meta) {
			if (d.name.equals(this.name)) {
				Status st=d.getType().validate(this.value);
				if (!st.isOk()){
					return new Status(Status.ERROR, 0, this.facetName()+":"+st.getMessage());
				}
				found=true;
			}
		}
		if (!found){
		return new Status(Status.ERROR, 0, "unknown facet with name " + this.name);
		}
		return Status.OK_STATUS;
	}

	/** {@inheritDoc} */
	@Override
	public Object value() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public AbstractType requiredType() {
		return BuiltIns.ANY;
	}
}
