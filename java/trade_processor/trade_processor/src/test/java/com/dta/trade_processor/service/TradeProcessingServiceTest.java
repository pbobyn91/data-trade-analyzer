package com.dta.trade_processor.service;

import com.dta.trade_processor.model.ProcessedTradeEvent;
import com.dta.trade_processor.model.RawTradeEvent;
import com.dta.trade_processor.model.RiskScore;
import com.dta.trade_processor.service.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeProcessingServiceTest {

    @InjectMocks
    private TradeProcessingService tradeProcessingService;

    @Mock
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource({
            "15.0, 100",
            "50.0, 1",
            "10, 1"
    })
    void validateAndParse_ValidTrade_ReturnsValid(double price, int volume) throws Exception {
        // Given
        String inputJson = "valid json";
        RawTradeEvent mockTrade = new RawTradeEvent();
        mockTrade.setPrice(price);
        mockTrade.setVolume(volume);
        when(objectMapper.readValue(inputJson, RawTradeEvent.class)).thenReturn(mockTrade);

        // When
        ValidationResult result = tradeProcessingService.validateAndParse(inputJson);

        // Then
        assertTrue(result.isValid());
        assertEquals(inputJson, result.getOriginalMessage());
        assertEquals(mockTrade, result.getRawTradeEvent());
    }

    @ParameterizedTest
    @CsvSource({
            "5.0, 100",
            "9.99, 100",
            "15.0, 0",
            "15.0, -1",
            "5.0, 0"
    })
    void validateAndParse_InvalidTrade_ReturnsInvalid(double price, int volume) throws Exception {
        // Given
        String invalidJson = "invalid json";
        RawTradeEvent mockTrade = new RawTradeEvent();
        mockTrade.setPrice(price);
        mockTrade.setVolume(volume);
        when(objectMapper.readValue(invalidJson, RawTradeEvent.class)).thenReturn(mockTrade);

        // When
        ValidationResult result = tradeProcessingService.validateAndParse(invalidJson);

        // Then
        assertFalse(result.isValid());
        assertEquals(invalidJson, result.getOriginalMessage());
        assertNull(result.getRawTradeEvent());
    }

    @Test
    void validateAndParse_JsonParsingError_ReturnsInvalid() throws Exception {
        // Given
        String malformedJson = "not a json";
        when(objectMapper.readValue(malformedJson, RawTradeEvent.class)).thenThrow(
                new JsonProcessingException("Parsing error") {});

        // When
        ValidationResult result = tradeProcessingService.validateAndParse(malformedJson);

        // Then
        assertFalse(result.isValid());
        assertEquals(malformedJson, result.getOriginalMessage());
    }

    @Test
    void processValidTrade_NullTradeId_HandlesGracefully() throws Exception {
        // Setup real ObjectMapper for this test
        ObjectMapper realObjectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(tradeProcessingService, "objectMapper", realObjectMapper);

        // Given
        RawTradeEvent rawTrade = new RawTradeEvent();
        rawTrade.setTradeId(null);
        rawTrade.setSymbol("AAPL");
        rawTrade.setPrice(15.0);
        rawTrade.setVolume(100);
        rawTrade.setTimestamp(1234567890L);

        // When
        String result = tradeProcessingService.processValidTrade(rawTrade);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("\"tradeId\":null"));

        ProcessedTradeEvent processed = realObjectMapper.readValue(result, ProcessedTradeEvent.class);
        assertNull(processed.getTradeId());
        assertEquals(RiskScore.LOW, processed.getRiskScore());
    }

    @Test
    void processValidTrade_ValidInput_ReturnsProcessedJson() throws Exception {
        // Setup real ObjectMapper for this test
        ObjectMapper realObjectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(tradeProcessingService, "objectMapper", realObjectMapper);

        // Given
        RawTradeEvent rawTrade = new RawTradeEvent();
        rawTrade.setTradeId("123");
        rawTrade.setSymbol("AAPL");
        rawTrade.setPrice(15.0);
        rawTrade.setVolume(100);
        rawTrade.setTimestamp(1234567890L);

        // When
        String result = tradeProcessingService.processValidTrade(rawTrade);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("\"tradeId\":\"123\""));
        assertTrue(result.contains("\"riskScore\":\"LOW\""));

        ProcessedTradeEvent processed = realObjectMapper.readValue(result, ProcessedTradeEvent.class);
        assertEquals("123", processed.getTradeId());
        assertEquals(RiskScore.LOW, processed.getRiskScore());
    }

    @Test
    void processValidTrade_HighRiskTrade_ReturnsProcessedJsonWithHighRisk() throws Exception {
        // Setup real ObjectMapper for this test
        ObjectMapper realObjectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(tradeProcessingService, "objectMapper", realObjectMapper);

        // Given
        RawTradeEvent rawTrade = new RawTradeEvent();
        rawTrade.setTradeId("456");
        rawTrade.setSymbol("GOOGL");
        rawTrade.setPrice(5.0);  // < 10.00 = HIGH risk
        rawTrade.setVolume(50);
        rawTrade.setTimestamp(1234567890L);

        // When
        String result = tradeProcessingService.processValidTrade(rawTrade);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("\"riskScore\":\"HIGH\""));

        ProcessedTradeEvent processed = realObjectMapper.readValue(result, ProcessedTradeEvent.class);
        assertEquals(RiskScore.HIGH, processed.getRiskScore());
    }

    @Test
    void processValidTrade_SerializationError_ThrowsRuntimeException() throws Exception {
        // Create a broken ObjectMapper that will fail serialization
        ObjectMapper brokenObjectMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("Serialization failed") {};
            }
        };

        ReflectionTestUtils.setField(tradeProcessingService, "objectMapper", brokenObjectMapper);

        // Given
        RawTradeEvent rawTrade = new RawTradeEvent();
        rawTrade.setTradeId("123");
        rawTrade.setSymbol("AAPL");
        rawTrade.setPrice(15.0);
        rawTrade.setVolume(100);
        rawTrade.setTimestamp(1234567890L);

        // When/Then
        assertThrows(RuntimeException.class, () ->
                tradeProcessingService.processValidTrade(rawTrade));
    }

}
