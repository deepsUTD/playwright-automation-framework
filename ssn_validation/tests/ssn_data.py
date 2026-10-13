# Format-only examples; these do not verify whether an SSN was issued.
invalid_ssns = [
    "",                 # empty
    "12345678",         # too short
    "1234567890",       # too long
    "000-12-3456",      # forbidden area number
    "666-12-3456",      # forbidden area number
    "900-12-3456",      # forbidden area range
    "123-00-4567",      # forbidden group number
    "123-45-0000",      # forbidden serial number
    "12A-45-6789",      # non-digit character
    "123 45 6789",      # incorrect separators
]

valid_format_ssns = [
    "123-45-6789",
    "234-56-7890",
    "345-67-8901",
]
