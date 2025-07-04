document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("imageUpload");

  if (!fileInput) return;

  let preview = document.getElementById("imagePreview");

  fileInput.addEventListener("change", function () {
    const file = this.files[0];

    if (file && file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = function (e) {
        // Nếu chưa có thẻ preview (do create.html không có sẵn ảnh), thì tạo mới
        if (!preview) {
          preview = document.createElement("img");
          preview.id = "imagePreview";
          preview.className = "img-thumbnail mt-3";
          preview.style.maxHeight = "150px";
          preview.style.borderRadius = "8px";

          // Thêm ảnh vào sau input group
          fileInput.closest(".col-sm-10").appendChild(preview);
        }

        preview.src = e.target.result;
        preview.style.display = "block";
      };
      reader.readAsDataURL(file);
    } else {
      if (preview) {
        preview.src = "#";
        preview.style.display = "none";
      }
    }
  });
});
