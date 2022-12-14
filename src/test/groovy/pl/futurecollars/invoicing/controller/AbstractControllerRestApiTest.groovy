package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.futurecollars.invoicing.db.file.JsonService
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import spock.lang.Specification

@AutoConfigureMockMvc
class AbstractControllerRestApiTest extends Specification {

    final String INVOICES_ENDPOINT = "/invoices/"
    final String TAX_ENDPOINT = "/tax/"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    long postInvoice(Invoice invoice) {
        mockMvc.perform(MockMvcRequestBuilders.post(INVOICES_ENDPOINT).content(getInvoiceAsJson(invoice)).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().response.contentAsString as long
    }

    Invoice getInvoice(long id) {
        def invoiceAsJson = mockMvc.perform(MockMvcRequestBuilders.get("$INVOICES_ENDPOINT$id"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().response.contentAsString

        return getInvoiceAsObject(invoiceAsJson)
    }

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(MockMvcRequestBuilders.get("$INVOICES_ENDPOINT" + "all"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().response.contentAsString
        return jsonService.convertToObject(response, Invoice[])
    }

    void deleteInvoice(long id) {
        mockMvc.perform(MockMvcRequestBuilders.delete("$INVOICES_ENDPOINT$id"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    void updateInvoice(long id, Invoice invoice) {
        mockMvc.perform(MockMvcRequestBuilders.put("$INVOICES_ENDPOINT$id").content(getInvoiceAsJson(invoice)).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    TaxCalculatorResult getTaxCalculatorResult(Company company) {
        def taxCalculatorResponse = mockMvc.perform(MockMvcRequestBuilders.post("$TAX_ENDPOINT").content(jsonService.convertToJson(company)).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().response.contentAsString

        return jsonService.convertToObject(taxCalculatorResponse, TaxCalculatorResult)
    }

    boolean compareInvoices(Invoice first, Invoice second) {
        first.getDate().toString() == second.getDate().toString()
        first.getSeller().toString() == second.getSeller().toString()
        first.getBuyer().toString() == second.getBuyer().toString()
        first.getInvoiceEntries().toString() == second.getInvoiceEntries().toString()
    }

    def getInvoiceAsJson(Invoice invoice) {
        return jsonService.convertToJson(invoice)
    }

    def getInvoiceAsObject(String json) {
        return jsonService.convertToObject(json, Invoice)
    }
}
