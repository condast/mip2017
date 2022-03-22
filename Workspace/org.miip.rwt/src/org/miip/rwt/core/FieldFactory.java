package org.miip.rwt.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringStyler;

public class FieldFactory {

	public static final String S_FIELD_PATH = "/resources/field/field.txt";

	public enum Attributes{
		ID,
		LATITUDE,
		LONGITUDE,
		LENGTH,
		WIDTH;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
	}

	public static IField createField() {
		Collection<IField> fields = new ArrayList<>();
		Scanner scanner = new Scanner( FieldFactory.class.getResourceAsStream(S_FIELD_PATH));
		try {
			while( scanner.hasNextLine() ) {
				String[] split = scanner.nextLine().split("[,]");
				Map<Attributes, String> convert = new HashMap<>();
				for( String str: split ) {
					String[] keyval = str.split("[=]" );
					Attributes attr = Attributes.valueOf(StringStyler.styleToEnum(keyval[0].trim()));
					convert.put(attr, keyval[1].trim());
				}
				LatLng latlng = new LatLng( convert.get(Attributes.ID ), Double.parseDouble( convert.get(Attributes.LATITUDE)), Double.parseDouble( convert.get(Attributes.LONGITUDE)));
				IField field = new Field( latlng, Integer.parseInt( convert.get(Attributes.LENGTH )), Integer.parseInt( convert.get(Attributes.WIDTH )));
				fields.add(field);
			}
		}
		finally {
			scanner.close();
		}
		return fields.iterator().next();
	}

}
