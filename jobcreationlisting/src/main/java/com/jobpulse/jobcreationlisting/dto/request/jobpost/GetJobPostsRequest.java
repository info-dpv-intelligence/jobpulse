package com.jobpulse.jobcreationlisting.dto.request.jobpost;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetJobPostsRequest {
    @Schema(description = "Cursor for pagination", example = "MTY2MzE0NjM3NzE4OQ==")
    private String cursor;

    @Min(1)
    @Schema(description = "Number of items to return", example = "10", defaultValue = "10")
    private Integer limit = 10;

    @Schema(description = "Field to sort by", defaultValue = "createdAt", allowableValues = {"createdAt", "title", "company"})
    private String sortField = "createdAt";

    @Schema(description = "Sort direction", defaultValue = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "DESC";
}