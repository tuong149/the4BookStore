package vn.bookstore.the4bookstore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.KhachHang;
import vn.bookstore.the4bookstore.entity.TaiKhoan;
import vn.bookstore.the4bookstore.repository.KhachHangRepository;
import vn.bookstore.the4bookstore.repository.TaiKhoanRepository;
import vn.bookstore.the4bookstore.security.CustomOAuth2User;
import vn.bookstore.the4bookstore.security.CustomOidcUser;
import vn.bookstore.the4bookstore.security.CustomUserDetails;
import vn.bookstore.the4bookstore.service.DanhMucService;
import vn.bookstore.the4bookstore.service.GioHangService;
import vn.bookstore.the4bookstore.service.SanPhamService;
import vn.bookstore.the4bookstore.service.ShopService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired(required = false)
    private KhachHangRepository khachHangRepository;
    @Autowired(required = false)
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired(required = false)
    private DanhMucService danhMucService;
    @Autowired(required = false)
    private SanPhamService sanPhamService;
    @Autowired(required = false)
    private GioHangService gioHangService;
    @Autowired(required = false)
    private ShopService shopService;

    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                    return;
                }
                try {
                    String clean = text.trim();
                    String digitsOnly = clean.replaceAll("[.,\\s]", "");
                    if (!digitsOnly.isEmpty()) {
                        setValue(Integer.parseInt(digitsOnly));
                    } else {
                        setValue(null);
                    }
                } catch (Exception e) {
                    try {
                        setValue((int) Math.round(Double.parseDouble(text.trim())));
                    } catch (Exception ex) {
                        setValue(null);
                    }
                }
            }
        });
    }

    public GlobalControllerAdvice() {
    }

    public GlobalControllerAdvice(KhachHangRepository khachHangRepository,
                                  TaiKhoanRepository taiKhoanRepository,
                                  DanhMucService danhMucService,
                                  SanPhamService sanPhamService,
                                  GioHangService gioHangService,
                                  ShopService shopService) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.danhMucService = danhMucService;
        this.sanPhamService = sanPhamService;
        this.gioHangService = gioHangService;
        this.shopService = shopService;
    }

    /**
     * Tối ưu: Chỉ đếm shop chờ duyệt đối với tài khoản Quản trị viên (ADMIN, MANAGER),
     * đồng thời sử dụng Cache để tránh query liên tục ở mọi request.
     */
    @ModelAttribute("pendingShopCount")
    public long getPendingShopCount(Authentication authentication) {
        if (shopService == null || authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        boolean canView = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("MANAGER"));
        if (!canView) return 0;

        try {
            return shopService.countPendingShops();
        } catch (Exception e) {
            return 0;
        }
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount(Authentication authentication, HttpSession session, HttpServletRequest request) {
        if (gioHangService == null) return 0;
        KhachHang kh = getCachedKhachHang(authentication, request);
        if (kh != null) {
            return gioHangService.getCartItemCount(kh);
        }
        return 0;
    }

    /**
     * Tối ưu Request-Scope Caching:
     * Tái sử dụng đối tượng KhachHang trong cùng 1 HTTP Request thay vì gọi findByTaiKhoan 3-4 lần.
     */
    private KhachHang getCachedKhachHang(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        if (request != null) {
            Object cached = request.getAttribute("CURRENT_KHACH_HANG_CACHE");
            if (cached instanceof KhachHang kh) {
                return kh;
            }
            if (Boolean.TRUE.equals(request.getAttribute("CURRENT_KHACH_HANG_CACHE_NULL"))) {
                return null;
            }
        }

        KhachHang kh = getKhachHangFromAuth(authentication);
        if (request != null) {
            if (kh != null) {
                request.setAttribute("CURRENT_KHACH_HANG_CACHE", kh);
            } else {
                request.setAttribute("CURRENT_KHACH_HANG_CACHE_NULL", Boolean.TRUE);
            }
        }
        return kh;
    }

    private KhachHang getKhachHangFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails && userDetails.getTaiKhoan() != null) {
            return khachHangRepository.findByTaiKhoan(userDetails.getTaiKhoan()).orElse(null);
        }
        if (principal instanceof CustomOAuth2User oAuth2User && oAuth2User.getTaiKhoan() != null) {
            return khachHangRepository.findByTaiKhoan(oAuth2User.getTaiKhoan()).orElse(null);
        }
        if (principal instanceof CustomOidcUser oidcUser && oidcUser.getTaiKhoan() != null) {
            return khachHangRepository.findByTaiKhoan(oidcUser.getTaiKhoan()).orElse(null);
        }
        String name = authentication.getName();
        if (name != null) {
            Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByEmail(name).or(() -> taiKhoanRepository.findByTenDangNhap(name));
            if (tkOpt.isPresent()) {
                return khachHangRepository.findByTaiKhoan(tkOpt.get()).orElse(null);
            }
        }
        return null;
    }

    /**
     * Tối ưu: Lấy danh mục từ DanhMucService & SanPhamService đã được @Cacheable trong bộ nhớ.
     * Loại bỏ 8-10 câu SQL gửi qua Internet cho mỗi request navbar.
     */
    @ModelAttribute("allActiveCategories")
    public List<DanhMuc> getAllActiveCategories() {
        if (danhMucService == null) return List.of();
        return danhMucService.getAllActive();
    }

    @ModelAttribute("navbarCategoriesSach")
    public List<DanhMuc> getNavbarCategoriesSach() {
        if (sanPhamService == null) return List.of();
        return sanPhamService.getCategoriesByLoaiSP("Sach");
    }

    @ModelAttribute("navbarCategoriesVpp")
    public List<DanhMuc> getNavbarCategoriesVpp() {
        if (sanPhamService == null) return List.of();
        return sanPhamService.getCategoriesByLoaiSP("VanPhongPham");
    }

    @ModelAttribute("navbarCategoriesQuaTang")
    public List<DanhMuc> getNavbarCategoriesQuaTang() {
        if (sanPhamService == null) return List.of();
        return sanPhamService.getCategoriesByLoaiSP("QuaTang");
    }

    @ModelAttribute("navbarCategories")
    public List<DanhMuc> getNavbarCategories() {
        return getAllActiveCategories();
    }

    @ModelAttribute("allMajorCategories")
    public List<vn.bookstore.the4bookstore.entity.DanhMucLon> getAllMajorCategories() {
        if (danhMucService == null) return List.of();
        return danhMucService.getActiveMajorCategories();
    }

    public record MajorCategoryGroup(
            vn.bookstore.the4bookstore.entity.DanhMucLon major,
            List<DanhMuc> subs
    ) {}

    @ModelAttribute("majorCategoryGroups")
    public List<MajorCategoryGroup> getMajorCategoryGroups() {
        if (danhMucService == null) return List.of();
        List<vn.bookstore.the4bookstore.entity.DanhMucLon> majors = danhMucService.getActiveMajorCategories();
        List<DanhMuc> allActive = getAllActiveCategories();
        if (majors == null || majors.isEmpty()) return List.of();

        return majors.stream().map(m -> {
            List<DanhMuc> subs = allActive.stream()
                    .filter(dm -> m.getMaLoai() != null && m.getMaLoai().equalsIgnoreCase(dm.getLoaiSP()))
                    .toList();
            return new MajorCategoryGroup(m, subs);
        }).toList();
    }

    private String getCustomAvatar(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        KhachHang cachedKh = getCachedKhachHang(authentication, request);

        if (principal instanceof CustomOAuth2User oAuth2User) {
            String picture = oAuth2User.getPicture();
            if (picture != null && !picture.isBlank() && !picture.contains("manager-avatar.png")) {
                return picture;
            }
            if (cachedKh != null && cachedKh.getAnhDaiDien() != null && !cachedKh.getAnhDaiDien().isBlank() && !cachedKh.getAnhDaiDien().contains("manager-avatar.png")) {
                return cachedKh.getAnhDaiDien();
            }
        }

        if (principal instanceof CustomOidcUser oidcUser) {
            String picture = oidcUser.getPicture();
            if (picture != null && !picture.isBlank() && !picture.contains("manager-avatar.png")) {
                return picture;
            }
            if (cachedKh != null && cachedKh.getAnhDaiDien() != null && !cachedKh.getAnhDaiDien().isBlank() && !cachedKh.getAnhDaiDien().contains("manager-avatar.png")) {
                return cachedKh.getAnhDaiDien();
            }
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object pic = oauth2User.getAttribute("picture");
            if (pic == null) {
                pic = oauth2User.getAttribute("avatar_url");
            }
            if (pic != null && !pic.toString().isBlank() && !pic.toString().contains("manager-avatar.png")) {
                return pic.toString();
            }
            if (cachedKh != null && cachedKh.getAnhDaiDien() != null && !cachedKh.getAnhDaiDien().isBlank() && !cachedKh.getAnhDaiDien().contains("manager-avatar.png")) {
                return cachedKh.getAnhDaiDien();
            }
        }

        if (principal instanceof CustomUserDetails) {
            if (cachedKh != null && cachedKh.getAnhDaiDien() != null && !cachedKh.getAnhDaiDien().isBlank() && !cachedKh.getAnhDaiDien().contains("manager-avatar.png")) {
                return cachedKh.getAnhDaiDien();
            }
        }

        return null;
    }

    @ModelAttribute("userAvatar")
    public String getUserAvatar(Authentication authentication, HttpServletRequest request) {
        String customAvatar = getCustomAvatar(authentication, request);
        if (customAvatar != null) {
            return customAvatar;
        }
        return vn.bookstore.the4bookstore.util.AvatarUtils.generateDefaultAvatarSvg();
    }

    @ModelAttribute("userInitial")
    public String getUserInitial(Authentication authentication, HttpServletRequest request) {
        String displayName = getUserDisplayName(authentication, request);
        return vn.bookstore.the4bookstore.util.AvatarUtils.extractInitial(displayName);
    }

    @ModelAttribute("userHasCustomAvatar")
    public boolean getUserHasCustomAvatar(Authentication authentication, HttpServletRequest request) {
        String customAvatar = getCustomAvatar(authentication, request);
        return vn.bookstore.the4bookstore.util.AvatarUtils.hasCustomAvatar(customAvatar);
    }

    @ModelAttribute("currentShop")
    public vn.bookstore.the4bookstore.entity.Shop getCurrentShop(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        if (shopService == null) return null;

        TaiKhoan tk = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            tk = userDetails.getTaiKhoan();
        } else if (principal instanceof CustomOAuth2User oAuth2User) {
            tk = oAuth2User.getTaiKhoan();
        } else if (principal instanceof CustomOidcUser oidcUser) {
            tk = oidcUser.getTaiKhoan();
        }
        if (tk == null && authentication.getName() != null) {
            String name = authentication.getName();
            tk = taiKhoanRepository.findByEmail(name).or(() -> taiKhoanRepository.findByTenDangNhap(name)).orElse(null);
        }
        if (tk != null) {
            Optional<vn.bookstore.the4bookstore.entity.Shop> userShop = shopService.findByTaiKhoan(tk);
            if (userShop.isPresent()) {
                return userShop.get();
            }
        }

        KhachHang kh = getCachedKhachHang(authentication, request);
        if (kh != null && kh.getTaiKhoan() != null) {
            Optional<vn.bookstore.the4bookstore.entity.Shop> userShop = shopService.findByTaiKhoan(kh.getTaiKhoan());
            if (userShop.isPresent()) {
                return userShop.get();
            }
        }
        return null;
    }


    @ModelAttribute("userDisplayName")
    public String getUserDisplayName(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "User";
        }

        Object principal = authentication.getPrincipal();
        KhachHang cachedKh = getCachedKhachHang(authentication, request);

        if (cachedKh != null && cachedKh.getHoTen() != null && !cachedKh.getHoTen().isBlank() && !cachedKh.getHoTen().matches("\\d+")) {
            return cachedKh.getHoTen();
        }

        if (principal instanceof CustomOAuth2User oAuth2User) {
            String name = oAuth2User.getName();
            if (name != null && !name.isBlank() && !name.matches("\\d+")) {
                return name;
            }
        }

        if (principal instanceof CustomOidcUser oidcUser) {
            String name = oidcUser.getName();
            if (name != null && !name.isBlank() && !name.matches("\\d+")) {
                return name;
            }
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object name = oauth2User.getAttribute("name");
            if (name != null && !name.toString().isBlank() && !name.toString().matches("\\d+")) {
                return name.toString();
            }
            Object given = oauth2User.getAttribute("given_name");
            Object family = oauth2User.getAttribute("family_name");
            if (given != null || family != null) {
                String full = ((family != null ? family.toString() + " " : "") + (given != null ? given.toString() : "")).trim();
                if (!full.isBlank() && !full.matches("\\d+")) {
                    return full;
                }
            }
            Object emailObj = oauth2User.getAttribute("email");
            if (emailObj != null) {
                return emailObj.toString().split("@")[0];
            }
        }

        if (principal instanceof CustomUserDetails userDetails) {
            if (userDetails.getUsername() != null && !userDetails.getUsername().matches("\\d+")) {
                return userDetails.getUsername();
            }
            if (userDetails.getTaiKhoan() != null && userDetails.getTaiKhoan().getEmail() != null) {
                return userDetails.getTaiKhoan().getEmail().split("@")[0];
            }
        }

        String authName = authentication.getName();
        return (authName != null && !authName.matches("\\d+") && !authName.isBlank()) ? authName : "User";
    }
}
