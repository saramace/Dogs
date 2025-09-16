package com.example.dogs.api

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory // Your converter
import okhttp3.MediaType.Companion.toMediaType

class DogApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var dogApi: DogApi
    private val json =
        Json { ignoreUnknownKeys = true } // Configure as per your app's Json instance

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder().build()

        dogApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DogApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getDogBreedList requests correct path and parameters, deserializes response`() = runTest {
        val pageNum = 1
        val pageSize = 10
        val mockJsonResponse = """ 
        {
          "data": [
            {
              "id": "1",
              "type": "breed",
              "attributes": { "name": "Beagle", "description": "A good dog", "life": {"min":10, "max":15}, "male_weight": {"min":10, "max":12}, "female_weight": {"min":8, "max":10}, "hypoallergenic": false },
              "relationships": {}
            }
          ],
          "meta": { "pagination": { "total": 1, "count": 1, "per_page": 10, "current": 1, "next": "next", "prev": "prev", "records":1 } },
          "links": { "self": "url", "current": "url", "next": "next", "last": "url"}
        }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockJsonResponse).setResponseCode(200))

        val response = dogApi.getDogBreedList(pageNumber = pageNum, pageSize = pageSize)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals(
            "/breeds?page%5Bnumber%5D=$pageNum&page%5Bsize%5D=$pageSize",
            recordedRequest.path
        )
        assertEquals("GET", recordedRequest.method)

        assertNotNull(response)
        assertEquals(1, response.data.size)
        assertEquals("Beagle", response.data[0].attributes.name)
        assertEquals(1, response.meta.pagination.current)
    }

    @Test
    fun `getDogBreedDetailsById requests correct path, deserializes response`() = runTest {
        val dogId = "a1b2c3d4"
        val mockJsonResponse = """
        {
          "data": {
            "id": "$dogId",
            "type": "breed_detail",
            "attributes": { "name": "Poodle", "description": "A fluffy dog", "life": {"min":12, "max":16}, "male_weight": {"min":20, "max":25}, "female_weight": {"min":18, "max":22}, "hypoallergenic": true },
            "relationships": { "group": { "data": {"id": "grp1", "type": "group"} } }
          }
        }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockJsonResponse).setResponseCode(200))

        val response = dogApi.getDogBreedDetailsById(dogId)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/breeds/$dogId", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)

        assertNotNull(response)
        assertEquals(dogId, response.data.id)
        assertEquals("Poodle", response.data.attributes.name)
        assertTrue(response.data.attributes.hypoallergenic)
        assertEquals("grp1", response.data.relationships?.group?.data?.id)
    }

    @Test
    fun `getDogBreedList handles API error (e_g_, 404)`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(404).setBody("{\"error\":\"Not Found\"}")
        )

        try {
            dogApi.getDogBreedList(pageNumber = 1, pageSize = 10)
            fail("HttpException was expected but not thrown.")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
        }
    }
}