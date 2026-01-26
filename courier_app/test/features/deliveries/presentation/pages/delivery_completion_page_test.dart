import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:get_it/get_it.dart';

import 'package:courier_app/features/deliveries/presentation/pages/delivery_completion_page.dart';
import 'package:courier_app/features/deliveries/presentation/bloc/delivery_bloc.dart';
import 'package:courier_app/features/deliveries/presentation/bloc/delivery_state.dart';
import 'package:courier_app/features/deliveries/domain/entities/delivery.dart';
import 'package:courier_app/core/services/image_upload_service.dart';

import 'delivery_completion_page_test.mocks.dart';

@GenerateMocks([DeliveryBloc, ImageUploadService])
void main() {
  late MockDeliveryBloc mockDeliveryBloc;
  late MockImageUploadService mockImageUploadService;
  late Delivery testDelivery;

  setUp(() {
    mockDeliveryBloc = MockDeliveryBloc();
    mockImageUploadService = MockImageUploadService();
    
    // Register mock services
    GetIt.instance.reset();
    GetIt.instance.registerSingleton<ImageUploadService>(mockImageUploadService);
    
    testDelivery = Delivery(
      deliveryId: 'test-delivery-id',
      orderId: 'test-order-id-12345',
      merchantName: 'Test Restaurant',
      merchantAddress: '123 Test Street',
      customerName: 'John Doe',
      customerPhone: '+258123456789',
      deliveryAddress: '456 Customer Street, Maputo',
      deliveryLatitude: -25.9692,
      deliveryLongitude: 32.5732,
      status: DeliveryStatus.outForDelivery,
      totalAmount: 250.0,
      currency: 'MZN',
      estimatedPickupTime: DateTime(2024, 1, 15, 12, 0),
      estimatedDeliveryTime: DateTime(2024, 1, 15, 13, 0),
      items: const [],
    );
  });

  tearDown(() {
    GetIt.instance.reset();
  });

  Widget createWidgetUnderTest() {
    return MaterialApp(
      home: BlocProvider<DeliveryBloc>(
        create: (context) => mockDeliveryBloc,
        child: DeliveryCompletionPage(delivery: testDelivery),
      ),
    );
  }

  group('DeliveryCompletionPage', () {
    testWidgets('should display delivery information correctly', (WidgetTester tester) async {
      // Arrange
      when(mockDeliveryBloc.state).thenReturn(DeliveryInitial());
      when(mockDeliveryBloc.stream).thenAnswer((_) => const Stream.empty());

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pumpAndSettle();

      // Assert
      expect(find.text('Complete Delivery'), findsOneWidget);
      expect(find.text('Delivery Information'), findsOneWidget);
      expect(find.text('Order #test-ord'), findsOneWidget);
      expect(find.text('John Doe'), findsOneWidget);
      expect(find.text('456 Customer Street, Maputo'), findsOneWidget);
    });

    testWidgets('should display confirmation code field', (WidgetTester tester) async {
      // Arrange
      when(mockDeliveryBloc.state).thenReturn(DeliveryInitial());
      when(mockDeliveryBloc.stream).thenAnswer((_) => const Stream.empty());

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pumpAndSettle();

      // Assert
      expect(find.text('Delivery Confirmation Code'), findsOneWidget);
      expect(find.text('Enter the 4-digit confirmation code provided by the customer'), findsOneWidget);
      expect(find.byType(TextFormField), findsAtLeastNWidgets(2)); // Code field and notes field
    });

    testWidgets('should display proof of delivery widget', (WidgetTester tester) async {
      // Arrange
      when(mockDeliveryBloc.state).thenReturn(DeliveryInitial());
      when(mockDeliveryBloc.stream).thenAnswer((_) => const Stream.empty());

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pumpAndSettle();

      // Assert
      expect(find.text('Proof of Delivery'), findsOneWidget);
      expect(find.text('Capture a photo or get customer signature as proof of delivery'), findsOneWidget);
      expect(find.text('Photo'), findsOneWidget);
      expect(find.text('Signature'), findsOneWidget);
    });

    testWidgets('should show location info section', (WidgetTester tester) async {
      // Arrange
      when(mockDeliveryBloc.state).thenReturn(DeliveryInitial());
      when(mockDeliveryBloc.stream).thenAnswer((_) => const Stream.empty());

      // Act
      await tester.pumpWidget(createWidgetUnderTest());
      await tester.pumpAndSettle();

      // Assert
      expect(find.text('Current Location'), findsOneWidget);
      expect(find.text('Getting current location...'), findsOneWidget);
    });
  });
}