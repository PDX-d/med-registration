package org.example.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PhoneDesensitizeSerializer extends JsonSerializer<String> {
	@Override
	public void serialize(String phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		String desensitizedPhone = DesensitizeUtil.phone(phone);
		jsonGenerator.writeString(desensitizedPhone);
	}
}
